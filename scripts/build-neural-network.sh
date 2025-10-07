#!/usr/bin/env bash

set -euo pipefail

# --- utils ---
die() { echo "ERROR: $*" >&2; exit 1; }
info() { echo -e "\033[1;34m[INFO]\033[0m $*"; }

# --- config / paths ---
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

NN_DIR="$ROOT/neural-network"
DST_DIR="$NN_DIR/dist"

CORE_LIBS="$ROOT/core/libs"

# --- jar and sources ---
OUT_JAR="$DST_DIR/neural-network.jar"
OUT_SRC_JAR="$DST_DIR/neural-network-sources.jar"

# build neural-network into a library JAR
bash "$NN_DIR/scripts/build.sh" --clean

mkdir -p "$CORE_LIBS"

cp -f "$OUT_JAR" "$CORE_LIBS/"
info "Copied binary jar to core/libs/"

cp -f "$OUT_SRC_JAR" "$CORE_LIBS/"
info "Copied sources jar to core/libs/"
