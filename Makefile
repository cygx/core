NAMES := $(patsubst core/%.java,%,$(wildcard core/*.java))

build:
	javac -d classes core/*.java

clean:
	rm -rf classes/*

test:
	javac -d classes test.java
	java -cp classes test

$(NAMES):
	javac -d classes core/$@.java
