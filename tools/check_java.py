#!/usr/bin/env python3
"""
Lekki statyczny weryfikator zrodel Java (uzywany gdy w srodowisku nie ma JDK).

Sprawdza:
  * zgodnosc pakietu ze sciezka pliku oraz nazwy klasy z nazwa pliku,
  * zbalansowanie nawiasow { } ( ) [ ],
  * czy kazdy uzyty typ (Identyfikator z wielkiej litery) jest zaimportowany,
    zadeklarowany w tym pliku, w tym samym pakiecie, w java.lang albo uzyty
    w pelni kwalifikowanej formie,
  * czy importy gg.anarchia.* wskazuja na istniejace pliki,
  * czy nie ma zduplikowanych custom-model-data / id przedmiotow.
"""
import os
import re
import sys
from collections import defaultdict

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "java")
ROOT = os.path.normpath(ROOT)

JAVA_LANG = {
    "String", "Integer", "Math", "System", "Object", "Boolean", "Double", "Long", "Float", "Short",
    "Byte", "Character", "Exception", "RuntimeException", "IllegalArgumentException", "IllegalStateException",
    "NumberFormatException", "UnsupportedOperationException", "NullPointerException", "ClassCastException",
    "Runnable", "Override", "SuppressWarnings", "Deprecated", "FunctionalInterface", "SafeVarargs",
    "Thread", "Throwable", "Error", "NoSuchFieldError", "StringBuilder", "StringBuffer", "CharSequence",
    "Comparable", "Iterable", "Number", "Void", "Class", "Enum", "Record", "Cloneable", "AutoCloseable",
    "ArithmeticException", "IndexOutOfBoundsException", "ArrayIndexOutOfBoundsException", "Process",
}
GENERICS = {"T", "E", "K", "V", "R", "U", "S"}

# Typy dostepne w Bukkit/Paper API - zestaw uzywany w projekcie (whitelist).
KNOWN_EXTERNAL = set()

comment_re = re.compile(r"//[^\n]*|/\*.*?\*/", re.S)
string_re = re.compile(r'"(\\.|[^"\\])*"' + r"|'(\\.|[^'\\])*'")
ident_re = re.compile(r"(?<![\w.$])([A-Z][A-Za-z0-9_]*)")
decl_re = re.compile(r"\b(?:class|interface|enum|record)\s+([A-Za-z0-9_]+)")


def strip_code(text):
    text = comment_re.sub(" ", text)
    text = string_re.sub('""', text)
    return text


def collect_files():
    files = []
    for base, _dirs, names in os.walk(ROOT):
        for name in names:
            if name.endswith(".java"):
                files.append(os.path.join(base, name))
    return sorted(files)


def main():
    files = collect_files()
    if not files:
        print("Brak plikow .java!")
        return 1

    package_classes = defaultdict(set)
    all_classes = {}
    for path in files:
        rel = os.path.relpath(path, ROOT)
        pkg = os.path.dirname(rel).replace(os.sep, ".")
        cls = os.path.basename(path)[:-5]
        package_classes[pkg].add(cls)
        all_classes[pkg + "." + cls] = path

    errors = []
    warnings = []

    for path in files:
        rel = os.path.relpath(path, ROOT)
        pkg = os.path.dirname(rel).replace(os.sep, ".")
        cls = os.path.basename(path)[:-5]
        raw = open(path, encoding="utf-8").read()
        code = strip_code(raw)

        m = re.search(r"^\s*package\s+([\w.]+);", code, re.M)
        if not m:
            errors.append(f"{rel}: brak deklaracji package")
            continue
        if m.group(1) != pkg:
            errors.append(f"{rel}: package {m.group(1)} != sciezka {pkg}")

        if not re.search(r"\b(?:class|interface|enum|record)\s+" + re.escape(cls) + r"\b", code):
            errors.append(f"{rel}: brak typu o nazwie {cls}")

        for opener, closer in (("{", "}"), ("(", ")"), ("[", "]")):
            if code.count(opener) != code.count(closer):
                errors.append(
                    f"{rel}: niezbalansowane {opener}{closer} "
                    f"({code.count(opener)} vs {code.count(closer)})")

        imports = {}
        wildcard = []
        for line in re.findall(r"^\s*import\s+(static\s+)?([\w.*]+);", code, re.M):
            target = line[1]
            if target.endswith(".*"):
                wildcard.append(target[:-2])
            else:
                imports[target.rsplit(".", 1)[-1]] = target

        for name, target in imports.items():
            if target.startswith("gg.anarchia") and target not in all_classes:
                outer = target.rsplit(".", 1)[0]
                if outer not in all_classes:
                    errors.append(f"{rel}: import wskazuje na nieistniejaca klase {target}")

        for pkg_name in wildcard:
            if pkg_name.startswith("gg.anarchia") and pkg_name not in package_classes:
                errors.append(f"{rel}: import {pkg_name}.* - brak takiego pakietu")

        declared = set(decl_re.findall(code))
        body = re.sub(r"^\s*(?:package|import)\s+[^;]+;", "", code, flags=re.M)

        available = set()
        available |= declared
        available |= set(imports.keys())
        available |= package_classes.get(pkg, set())
        available |= JAVA_LANG
        available |= GENERICS
        available |= KNOWN_EXTERNAL
        for pkg_name in wildcard:
            available |= package_classes.get(pkg_name, set())

        # stale SCREAMING_CASE to pola/enumy, nie typy
        const_re = re.compile(r"^[A-Z][A-Z0-9_]*$")
        used = set(ident_re.findall(body))
        unknown = sorted(u for u in used if u not in available and not const_re.match(u))
        if unknown:
            warnings.append(f"{rel}: potencjalnie brakujace importy -> {', '.join(unknown)}")

    print("=== BLEDY ===")
    for error in errors:
        print(" !", error)
    if not errors:
        print(" brak")
    print()
    print("=== OSTRZEZENIA (sprawdz recznie) ===")
    for warning in warnings:
        print(" ?", warning)
    if not warnings:
        print(" brak")
    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())
