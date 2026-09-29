#!/usr/bin/env python3
"""
Sprawdza spojnosc wewnetrznego API pluginu (bez kompilatora):
  * statyczne wywolania KlasaProjektu.metoda(...) - czy metoda istnieje,
  * wywolania przez akcesory plugin.items()/regions()/messages()/configs()/events()/blocks(),
  * czy kazda klasa przedmiotu jest zarejestrowana w ItemManager,
  * czy kazda klasa eventu jest zarejestrowana w EventManager.
"""
import os
import re
import sys
from collections import defaultdict

ROOT = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..",
                                     "src", "main", "java"))
PKG = "gg.anarchia.itemy"

comment_re = re.compile(r"//[^\n]*|/\*.*?\*/", re.S)
string_re = re.compile(r'"(\\.|[^"\\])*"')
method_re = re.compile(
    r"\b(?:public|protected|private)\s+(?:static\s+|final\s+|abstract\s+|synchronized\s+|default\s+)*"
    r"(?:<[^>]+>\s*)?[\w.<>\[\],?\s]+?\s+([a-zA-Z_][\w]*)\s*\(")
field_re = re.compile(r"\b(?:public|protected)\s+(?:static\s+)?(?:final\s+)?[\w.<>\[\],?]+\s+([A-Z_][A-Z0-9_]*)\s*[=;]")

ACCESSORS = {
    "items": "ItemManager",
    "regions": "RegionManager",
    "messages": "Messages",
    "configs": "ConfigManager",
    "events": "EventManager",
    "blocks": "BlockRestore",
}


def strip_code(text):
    return string_re.sub('""', comment_re.sub(" ", text))


def main():
    files = []
    for base, _dirs, names in os.walk(ROOT):
        for name in sorted(names):
            if name.endswith(".java"):
                files.append(os.path.join(base, name))

    methods = defaultdict(set)
    parents = {}
    sources = {}
    for path in files:
        cls = os.path.basename(path)[:-5]
        code = strip_code(open(path, encoding="utf-8").read())
        sources[cls] = code
        methods[cls] |= set(method_re.findall(code))
        methods[cls] |= set(field_re.findall(code))
        m = re.search(r"\bclass\s+" + re.escape(cls) + r"\b[^{]*?\bextends\s+([\w.]+)", code)
        if m:
            parents[cls] = m.group(1).split(".")[-1]

    def has(cls, name, seen=None):
        seen = seen or set()
        while cls and cls not in seen:
            seen.add(cls)
            if name in methods.get(cls, ()):
                return True
            cls = parents.get(cls)
        return False

    problems = []
    for path in files:
        rel = os.path.relpath(path, ROOT)
        cls = os.path.basename(path)[:-5]
        code = sources[cls]

        for owner, call in re.findall(r"(?<![\w.])([A-Z][A-Za-z0-9_]*)\.([a-z][\w]*)\s*\(", code):
            if owner not in methods or owner == cls:
                continue
            if not has(owner, call):
                problems.append(f"{rel}: {owner}.{call}(...) - brak takiej metody w {owner}")

        for accessor, target in ACCESSORS.items():
            pattern = r"(?:plugin|get\(\))\s*\.\s*" + accessor + r"\(\)\s*\.\s*([a-z][\w]*)\s*\("
            for call in re.findall(pattern, code):
                if not has(target, call):
                    problems.append(f"{rel}: .{accessor}().{call}(...) - brak metody w {target}")

    # handlery: czy klasa implementujaca Handlers.X ma wymagana metode
    handlers_src = sources.get("Handlers", "")
    handler_methods = {}
    for block in re.finditer(r"interface\s+([A-Za-z0-9_]+)\s*\{(.*?)\n    \}", handlers_src, re.S):
        names = re.findall(r"\b[\w.<>\[\]]+\s+([a-zA-Z_][\w]*)\s*\(", block.group(2))
        handler_methods[block.group(1)] = [n for n in names if n not in ("if", "for", "while", "return")]
    for path in files:
        rel = os.path.relpath(path, ROOT)
        cls = os.path.basename(path)[:-5]
        code = sources[cls]
        for handler in re.findall(r"Handlers\.([A-Za-z0-9_]+)", code):
            if handler not in handler_methods:
                problems.append(f"{rel}: Handlers.{handler} nie istnieje")
                continue
            if "implements" not in code:
                continue
            impl = re.search(r"class\s+" + re.escape(cls) + r"\b[^{]*implements([^{]*)\{", code)
            if not impl or ("Handlers." + handler) not in impl.group(1):
                continue
            for required in handler_methods[handler]:
                if not has(cls, required):
                    problems.append(f"{rel}: brak metody {required}(...) wymaganej przez Handlers.{handler}")

    # rejestracje
    manager = sources.get("ItemManager", "")
    registered = set(re.findall(r"register\(new\s+([A-Za-z0-9_]+)\(", manager))
    item_dir = os.path.join(ROOT, *PKG.split("."), "item", "items")
    item_classes = {n[:-5] for n in os.listdir(item_dir) if n.endswith(".java")}
    missing = sorted(item_classes - registered - {"ZbrojaItem"})
    if missing:
        problems.append("ItemManager: niezarejestrowane przedmioty -> " + ", ".join(missing))
    ghost = sorted(registered - item_classes)
    if ghost:
        problems.append("ItemManager: rejestruje nieistniejace klasy -> " + ", ".join(ghost))

    event_manager = sources.get("EventManager", "")
    registered_events = set(re.findall(r"register\(new\s+([A-Za-z0-9_]+)\(", event_manager))
    event_dir = os.path.join(ROOT, *PKG.split("."), "events")
    event_classes = {n[:-5] for n in os.listdir(event_dir) if n.endswith(".java")}
    missing_events = sorted(event_classes - registered_events - {"GameEvent", "EventManager"})
    if missing_events:
        problems.append("EventManager: niezarejestrowane eventy -> " + ", ".join(missing_events))

    print("=== PROBLEMY API ===")
    for problem in problems:
        print(" !", problem)
    if not problems:
        print(" brak")
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
