# Matching Algorithm

A Go implementation of a concurrent matching algorithm that matches residents to programs based on their rank-order lists (ROLs), available positions, and program preferences.

## Project Structure

```text
.
├── data/
│   ├── Residents*.csv
│   └── Programs*.csv
├── *.go
├── go.mod
└── README.md
```

## Requirements

- Go 1.XX or later

## Input Files

The program reads two CSV files from the `data/` folder:

- `Residents*.csv` — contains resident IDs, names, and their rank-order lists.
- `Programs*.csv` — contains program IDs, program names, available positions, and program rank-order lists.

The program expects the files to follow the naming format:

```text
data/Residents<parameter>.csv
data/Programs<parameter>.csv
```

For example, running:

```bash
go run . 1
```

will read:

```text
data/Residents1.csv
data/Programs1.csv
```

## Running the Program

Clone the repository and navigate into the project directory:

```bash
git clone <repository-url>
cd <project-directory>
```

Run the program with the desired dataset parameter:

```bash
go run . 1
```

Replace `1` with the parameter corresponding to the dataset you want to use.

## Output

The program outputs:

- Each resident's matched program
- Residents who were not matched
- The number of unmatched residents
- The number of available program positions
- The execution time
- The input files used

Example:

```text
lastname,firstname,residentID,programID,name
Smith,John,123,NMD,Neurology
Doe,Jane,456,XXX,NOT_MATCHED

Number of unmatched residents: 1
Number of positions available: 2

Execution time: 1.234ms

File: data/Residents1.csv and data/Programs1.csv
```

## Concurrency

The matching process uses Go goroutines and `sync.WaitGroup` to process resident offers concurrently.

Mutexes are used to protect shared resident and program data during the matching process.

## Main Components

### `Resident`

Represents a resident and contains:

- Resident ID
- First and last name
- Rank-order list
- Matched program
- Mutex for synchronization

### `Program`

Represents a program and contains:

- Program ID
- Program name
- Number of available positions
- Rank-order list
- Resident ranking map
- Selected residents
- Mutex for synchronization

### Matching Process

Residents make offers to programs in the order specified by their rank-order lists. Programs evaluate applicants according to their own ranking and available positions. If a resident is rejected, they continue to their next ranked program.

## Author

Vineeth Ravi
