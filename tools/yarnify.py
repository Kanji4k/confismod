"""Converts the mod source from Minecraft intermediary names (class_310, method_1551 ...)
into readable Yarn names so Gradle/Loom can compile it. Runs automatically in GitHub Actions."""
import io, os, re, sys, urllib.request, zipfile

YARN = "1.21.11+build.4"
URL = f"https://maven.fabricmc.net/net/fabricmc/yarn/{YARN}/yarn-{YARN}-v2.jar"
SRC, DST = "intermediary-src", "src/main/java"

print("Downloading", URL)
data = urllib.request.urlopen(URL).read()
tiny = zipfile.ZipFile(io.BytesIO(data)).read("mappings/mappings.tiny").decode("utf-8").splitlines()

classes, members = {}, {}
for line in tiny[1:]:
    p = line.split("\t")
    if p[0] == "c" and len(p) >= 3 and p[2]:
        classes[p[1]] = p[2]
    elif len(p) >= 5 and p[1] in ("m", "f") and p[4]:
        members[p[3]] = p[4]
print(len(classes), "classes,", len(members), "members mapped")

cls_pat = re.compile(r"net([./])minecraft\1(class_\d+(?:\$class_\d+)*)")
bare_pat = re.compile(r"\bclass_\d+(?:\$class_\d+)*\b")
mem_pat = re.compile(r"\b(?:method|field|comp)_\d+\b")

def full(inter):
    return classes.get("net/minecraft/" + inter)

def sub_full(m):
    sep, inter = m.group(1), m.group(2)
    named = full(inter)
    if not named:
        return m.group(0)
    return named.replace("/", sep) if sep == "/" else named.replace("/", ".").replace("$", ".")

def sub_bare(m):
    named = full(m.group(0))
    if not named:
        return m.group(0)
    return named.split("/")[-1].split("$")[-1]

def convert(text):
    text = cls_pat.sub(sub_full, text)
    text = bare_pat.sub(sub_bare, text)
    text = mem_pat.sub(lambda m: members.get(m.group(0), m.group(0)), text)
    return text

for root, _, files in os.walk(SRC):
    for f in files:
        if not f.endswith(".java"):
            continue
        sp = os.path.join(root, f)
        dp = os.path.join(DST, os.path.relpath(sp, SRC))
        os.makedirs(os.path.dirname(dp), exist_ok=True)
        with open(sp, encoding="utf-8") as fh:
            out = convert(fh.read())
        with open(dp, "w", encoding="utf-8") as fh:
            fh.write(out)
print("Converted sources written to", DST)
