NAMES := $(patsubst %.java,%,$(wildcard *.java))
build:; javac -d classes *.java
clean:; rm -rf classes/*
$(NAMES):; javac -d classes $@.java
