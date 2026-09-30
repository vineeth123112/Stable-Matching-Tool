//Student Name: Vineeth Ravi
//Student Number: 300380109

package main

import (
	"encoding/csv"
	"fmt"
	"os"
	"strconv"
	"strings"
	"sync"
	"time"
)

// The Resident data type
type Resident struct {
	residentID     int
	firstname      string
	lastname       string
	rol            []string // resident rank order list
	matchedProgram string   // will be "" for unmatched resident
	mutex          sync.Mutex
}

// The Program data type
type Program struct {
	programID         string
	name              string
	nPositions        int   // number of positions available (quota)
	rol               []int // program rank order list
	rank              map[int]int
	selectedResidents []int // TO ADD: a data structure for the selected resident IDs
	mutex             sync.Mutex
}

// Parse a resident's ROL
func parseRol(s string) []string {
	s = strings.TrimSpace(s)
	s = strings.TrimPrefix(s, "[")
	s = strings.TrimSuffix(s, "]")
	if s == "" {
		return []string{}
	}
	parts := strings.Split(s, ",")
	for i, part := range parts {
		parts[i] = strings.TrimSpace(part)
	}
	return parts
}

// Parse a program's ROL
func parseIntRol(s string) []int {
	s = strings.TrimSpace(s)
	s = strings.TrimPrefix(s, "[")
	s = strings.TrimSuffix(s, "]")
	if s == "" {
		return []int{}
	}
	parts := strings.Split(s, ",")
	var ints []int
	for _, part := range parts {
		pid, _ := strconv.Atoi(strings.TrimSpace(part))
		ints = append(ints, pid)
	}
	return ints
}

// ReadCSV reads a CSV file into a map of Resident
func ReadResidentsCSV(filename string) (map[int]*Resident, error) {

	// map to store residents by ID
	residents := make(map[int]*Resident)

	file, err := os.Open(filename)
	if err != nil {
		return nil, fmt.Errorf("unable to open file: %w", err)
	}
	defer file.Close()

	reader := csv.NewReader(file)

	// Read all records
	records, err := reader.ReadAll()
	if err != nil {
		return nil, fmt.Errorf("error reading CSV: %w", err)
	}

	// Skip header if present (assuming it is)
	for i, record := range records {
		if i == 0 && record[0] == "id" {
			continue
		}
		if len(record) < 4 {
			return nil, fmt.Errorf("invalid record at line %d: %v", i+1, record)
		}

		// Parse ID
		id, err := strconv.Atoi(record[0])
		if err != nil {
			return nil, fmt.Errorf("invalid ID at line %d: %w", i+1, err)
		}

		if _, exists := residents[id]; exists {
			fmt.Println(id)
		}

		residents[id] = &Resident{
			residentID:     id,
			firstname:      record[1],
			lastname:       record[2],
			rol:            parseRol(record[3]),
			matchedProgram: "",
		}
	}

	return residents, nil
}

// reads a CSV file into a map of Program
func ReadProgramsCSV(filename string) (map[string]*Program, error) {

	// map to store programs by ID
	programs := make(map[string]*Program)

	file, err := os.Open(filename)
	if err != nil {
		return nil, fmt.Errorf("unable to open file: %w", err)
	}
	defer file.Close()

	reader := csv.NewReader(file)

	// Read all records
	records, err := reader.ReadAll()
	if err != nil {
		return nil, fmt.Errorf("error reading CSV: %w", err)
	}

	// Skip header if present (assuming it is)
	for i, record := range records {
		if i == 0 && record[0] == "id" {
			continue
		}
		if len(record) < 4 {
			return nil, fmt.Errorf("invalid record at line %d: %v", i+1, record)
		}

		// Parse number of positions
		np, err := strconv.Atoi(record[2])
		if err != nil {
			return nil, fmt.Errorf("invalid number at line %d: %w", i+1, err)
		}
		rol := parseIntRol(record[3])
		rank := make(map[int]int)
		for i, rid := range rol {
			rank[rid] = i
		}
		programs[record[0]] = &Program{
			programID:         record[0],
			name:              record[1],
			nPositions:        np,
			rol:               rol,
			rank:              rank,
			selectedResidents: []int{},
		}

	}

	return programs, nil
}

func offer(rid int, residents map[int]*Resident, programs map[string]*Program, wg *sync.WaitGroup) {
	defer wg.Done()
	resident := residents[rid]

	if len(resident.rol) == 0 {
		return
	} else {
		pid := resident.rol[0]
		resident.rol = resident.rol[1:]
		evaluate(rid, pid, residents, programs, wg)
	}
}

func evaluate(rid int, pid string, residents map[int]*Resident, programs map[string]*Program, wg *sync.WaitGroup) {
	program := programs[pid]
	resident := residents[rid]

	program.mutex.Lock()

	if rankOfResident(rid, program) == -1 {
		program.mutex.Unlock()
		wg.Add(1)
		go offer(rid, residents, programs, wg)
	} else if len(program.selectedResidents) < program.nPositions {
		program.selectedResidents = append(program.selectedResidents, rid)
		program.mutex.Unlock()
		resident.matchedProgram = pid
		return
	} else if rankOfResident(rid, program) < rankOfWorstResident(program.selectedResidents, program) {
		worstId := idOfWorstResident(program.selectedResidents, program)
		for i, id := range program.selectedResidents {
			if id == worstId {
				program.selectedResidents[i] = rid
				program.mutex.Unlock()
				resident.matchedProgram = pid
				residents[worstId].matchedProgram = ""
				wg.Add(1)
				go offer(worstId, residents, programs, wg)
			}
		}
	} else {
		program.mutex.Unlock()
		wg.Add(1)
		go offer(rid, residents, programs, wg)
	}
}

func rankOfResident(rid int, program *Program) int {
	rank, ok := program.rank[rid]
	if ok {
		return rank
	}
	return -1
}

func rankOfWorstResident(selectedResidents []int, program *Program) int {
	worstId := selectedResidents[0]
	for _, id := range selectedResidents {
		if rankOfResident(id, program) > rankOfResident(worstId, program) {
			worstId = id
		}
	}
	return rankOfResident(worstId, program)
}
func idOfWorstResident(selectedResidents []int, program *Program) int {
	worstId := selectedResidents[0]
	for _, id := range selectedResidents {
		if rankOfResident(id, program) > rankOfResident(worstId, program) {
			worstId = id
		}
	}
	return worstId
}

// Example usage
func main() {

	// read residents
	residentsFile := "data/residents" + os.Args[1] + ".csv"
	residentsFile = strings.ToUpper(residentsFile[:1]) + residentsFile[1:]
	programsFile := "data/programs" + os.Args[1] + ".csv"
	programsFile = strings.ToUpper(programsFile[:1]) + programsFile[1:]

	residents, err := ReadResidentsCSV(residentsFile)
	if err != nil {
		fmt.Println("Error:", err)
		return
	}
	/*
		for _, p := range residents {
			fmt.Printf("ID: %d, Name: %s %s, Rol: %v\n", p.residentID, p.firstname, p.lastname, p.rol)
		}
	*/

	programs, err := ReadProgramsCSV(programsFile)
	if err != nil {
		fmt.Println("Error:", err)
		return
	}
	/*
		for _, p := range programs {
			fmt.Printf("ID: %s, Name: %s, Number of pos: %d, Number of applicants: %d\n", p.programID, p.name, p.nPositions, len(p.rol))
		}

		fmt.Printf("\nNMD: %v", programs["NMD"])
	*/
	start := time.Now()
	var wg sync.WaitGroup
	for rid := range residents {
		wg.Add(1)
		go offer(rid, residents, programs, &wg)
	}
	wg.Wait()
	end := time.Now()

	fmt.Println()
	unmatchedResidents := 0
	fmt.Println("lastname,firstname,residentID,programID,name")
	for _, resident := range residents {
		if resident.matchedProgram == "" {
			fmt.Printf("%s,%s,%d,XXX,NOT_MATCHED\n", resident.lastname, resident.firstname, resident.residentID)
			unmatchedResidents++
		} else {
			fmt.Printf("%s,%s,%d,%s,%s\n", resident.lastname, resident.firstname, resident.residentID, resident.matchedProgram, programs[resident.matchedProgram].name)
		}
	}
	fmt.Println()
	positionsAvailable := 0
	for _, program := range programs {
		positionsAvailable += program.nPositions - len(program.selectedResidents)
	}
	fmt.Printf("Number of unmatched residents: %d\n", unmatchedResidents)
	fmt.Printf("Number of positions available: %d\n", positionsAvailable)
	fmt.Println()
	fmt.Printf("Execution time: %s\n", end.Sub(start))
	fmt.Println()
	fmt.Println("File: " + residentsFile + " and " + programsFile)
}
