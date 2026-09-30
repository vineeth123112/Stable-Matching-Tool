# Matching Algorithm

A concurrent Go implementation of a matching algorithm that matches residents to programs based on rank-order lists, available positions, and program preferences.

For example:
```bash
go run . 1
```

uses `Residents1.csv` and `Programs1.csv`.

## Running

```bash
go run stable-matching-tool.go <size>
```
where size can be small, medium, or large

The program outputs each resident's match, unmatched residents, available positions, execution time, and input files.

## Author

Vineeth Ravi
