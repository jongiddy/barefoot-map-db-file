# Barefoot Map File Maker

Convert the contents of a Barefoot roads database to a file.

To use a file input to map match:
- use this code to create a roadmap file with a `.bfmap` suffix;
- set the database property `database.name` to the path of the roadmap file without the suffix;
- call `Loader.roadmap` with the second argument set to `true`.

