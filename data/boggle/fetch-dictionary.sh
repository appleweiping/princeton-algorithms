#!/usr/bin/env bash
# Downloads and prepares the English word list used by the Boggle solver.
# The word list (dwyl/english-words, Unlicense) is NOT committed to this repo;
# run this script once to produce data/boggle/dictionary-english.txt.
set -euo pipefail
cd "$(dirname "$0")"

URL="https://raw.githubusercontent.com/dwyl/english-words/master/words_alpha.txt"
echo "Downloading word list from $URL ..."
curl -sSL "$URL" -o words_alpha_raw.txt

echo "Normalizing to uppercase A-Z words of length >= 3 ..."
tr -d '\r' < words_alpha_raw.txt | tr 'a-z' 'A-Z' | grep -E '^[A-Z]{3,}$' | sort -u > dictionary-english.txt
rm -f words_alpha_raw.txt

wc -l dictionary-english.txt
echo "Done -> dictionary-english.txt"
