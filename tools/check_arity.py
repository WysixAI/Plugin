#!/usr/bin/env python3
"""
Sprawdza liczbe argumentow w wywolaniach wewnetrznego API pluginu:
  * statyczne metody klas projektu (np. ItemUtil.give(...)),
  * metody managerow przez akcesory plugin.items()/regions()/...,
  * konstruktory klas projektu (new PanelGui(...)).
"""
import glob
import os
import re
import sys

ROOT = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), "..",
                                     "src", "main", "java"))

ACCESSORS = {
    "items": "ItemManager",
    "regions": "RegionManager",
    "messages": "Messages",
    "configs": "ConfigManager",
    "events": "EventManager",
    "blocks": "BlockRestore",
}


def clean(text):
    text = re.sub(r"//[^\n]*|/\*.*?\*/", " ", text, flags=re.S)
    return re.sub(r'"(\\.|[^"\\])*"', '""', text)


def param_count(params):
    params = params.strip()
    if not params:
        return 0
    depth = 0
    count = 1
    for char in params:
        if char in "<([":
            depth += 1
        elif char in ">)]":
            depth -= 1
        elif char == "," and depth == 0:
            count += 1
    return -count if "..." in params else count


def arg_count(text, start):
    index = start
    depth = 1
    count = 1
    started = False
    while index < len(text) and depth > 0:
        char = text[index]
        if char in "([{":
            depth += 1
        elif char in ")]}":
            depth -= 1
        elif char == "," and depth == 1:
            count += 1
        if depth > 0 and not char.isspace():
            started = True
        index += 1
    return count if started else 0


def matches(allowed, count):
    for value in allowed:
        if value >= 0 and count == value:
            return True
        if value < 0 and count >= (-value) - 1:
            return True
    return False


def main():
    files = sorted(glob.glob(os.path.join(ROOT, "**", "*.java"), recursive=True))
    statics, instances, ctors, parents = {}, {}, {}, {}

    for path in files:
        cls = os.path.basename(path)[:-5]
        code = clean(open(path, encoding="utf-8").read())
        statics.setdefault(cls, {})
        instances.setdefault(cls, {})
        for match in re.finditer(
                r"\n\s*(public|protected)\s+(static\s+)?(?:final\s+|abstract\s+|synchronized\s+|default\s+)*"
                r"(?:<[^>]+>\s*)?[\w.<>\[\],?\s]+?\s+([a-zA-Z_]\w*)\s*\(([^)]*)\)", code):
            target = statics[cls] if match.group(2) else instances[cls]
            target.setdefault(match.group(3), set()).add(param_count(match.group(4)))
        for match in re.finditer(r"\n\s*(?:public|private|protected)\s+" + re.escape(cls) + r"\s*\(([^)]*)\)", code):
            ctors.setdefault(cls, set()).add(param_count(match.group(1)))
        parent = re.search(r"\bclass\s+" + re.escape(cls) + r"\b[^{]*?\bextends\s+([\w.]+)", code)
        if parent:
            parents[cls] = parent.group(1).split(".")[-1]

    def lookup(cls, name):
        allowed = set()
        seen = set()
        while cls and cls not in seen:
            seen.add(cls)
            allowed |= instances.get(cls, {}).get(name, set())
            allowed |= statics.get(cls, {}).get(name, set())
            cls = parents.get(cls)
        return allowed

    problems = []
    for path in files:
        rel = os.path.relpath(path, ROOT)
        code = clean(open(path, encoding="utf-8").read())

        for match in re.finditer(r"(?<![\w.])([A-Z]\w*)\.([a-z]\w*)\s*\(", code):
            cls, name = match.group(1), match.group(2)
            allowed = statics.get(cls, {}).get(name)
            if not allowed:
                continue
            count = arg_count(code, match.end())
            if not matches(allowed, count):
                problems.append(f"{rel}: {cls}.{name}({count}) - dostepne: {sorted(allowed)}")

        for accessor, target in ACCESSORS.items():
            pattern = r"(?:plugin|get\(\))\s*\.\s*" + accessor + r"\(\)\s*\.\s*([a-z]\w*)\s*\("
            for match in re.finditer(pattern, code):
                name = match.group(1)
                allowed = lookup(target, name)
                if not allowed:
                    problems.append(f"{rel}: {target}.{name}(...) nie istnieje")
                    continue
                count = arg_count(code, match.end())
                if not matches(allowed, count):
                    problems.append(f"{rel}: {target}.{name}({count}) - dostepne: {sorted(allowed)}")

        for match in re.finditer(r"\bnew\s+([A-Z]\w*)\s*\(", code):
            cls = match.group(1)
            if cls not in ctors:
                continue
            count = arg_count(code, match.end())
            if not matches(ctors[cls], count):
                problems.append(f"{rel}: new {cls}({count}) - dostepne: {sorted(ctors[cls])}")

    print("=== ARITY ===")
    for problem in sorted(set(problems)):
        print(" !", problem)
    if not problems:
        print(" brak")
    return 1 if problems else 0


if __name__ == "__main__":
    sys.exit(main())
