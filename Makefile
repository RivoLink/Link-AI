bnn := ./scripts/build-neural-network.sh

build-nn:
	$(bnn) --clean --copy-core
.PHONY: build-nn
