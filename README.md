# PNC Schema Generator

The goal of this project is to detect Schema changes in the PNC project between
commits, catch those changes and warn the sys-admins of the change.

This repository mostly uses code from:
- https://github.com/geowarin/hibernate-examples/tree/master/generate-ddl-hibernate
- Blog Post: https://geowarin.github.io/generate-ddl-with-hibernate.html

## How to use

1. Build latest PNC source code
2. Build this project. It will grab the latest model.jar from PNC
```
mvn clean install
```
3. Run:
```
java -jar target/pnc-schema-generator.jar
```
4. A file named `schema.sql` will be built.
5. By diffing the `schema.sql` produced between a previous commit and the
   current commit, we can know if there is a schema change
