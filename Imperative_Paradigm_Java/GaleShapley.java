// Project CSI2120/CSI2520
// Winter 2026
// Robert Laganiere, uottawa.ca

// Student Name: Vineeth Ravi
// Student Number: 300380109

import java.io.*;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

// this is the (incomplete) class that will generate the resident and program maps
public class GaleShapley {
	
	public HashMap<Integer,Resident> residents;
	public HashMap<String,Program> programs;
	public HashMap<Integer, String> matches;
	public ArrayList<Resident> unmatched;
	
	public GaleShapley(String residentsFilename, String programsFilename) throws IOException, 
													NumberFormatException {
		
		readResidents(residentsFilename);
		readPrograms(programsFilename);
	}
	
	// Reads the residents csv file
	// It populates the residents HashMap
    public void readResidents(String residentsFilename) throws IOException, 
													NumberFormatException {

        String line;
		residents= new HashMap<Integer,Resident>();
		BufferedReader br = new BufferedReader(new FileReader(residentsFilename)); 

		int residentID;
		String firstname;
		String lastname;
		String plist;
		String[] rol;

		// Read each line from the CSV file
		line = br.readLine(); // skipping first line
		while ((line = br.readLine()) != null && line.length() > 0) {

			int split;
			int i;

			// extracts the resident ID
			for (split=0; split < line.length(); split++) {
				if (line.charAt(split) == ',') {
					break;
				} 
			}
			if (split > line.length()-2)
				throw new IOException("Error: Invalid line format: " + line);

			residentID= Integer.parseInt(line.substring(0,split));
			split++;

			// extracts the resident firstname
			for (i= split ; i < line.length(); i++) {
				if (line.charAt(i) == ',') {
					break;
				} 
			}
			if (i > line.length()-2)
				throw new IOException("Error: Invalid line format: " + line);

			firstname= line.substring(split,i);
			split= i+1;
			
			// extracts the resident lastname
			for (i= split ; i < line.length(); i++) {
				if (line.charAt(i) == ',') {
					break;
				} 
			}
			if (i > line.length()-2)
				throw new IOException("Error: Invalid line format: " + line);

			lastname= line.substring(split,i);
			split= i+1;		
				
			Resident resident= new Resident(residentID,firstname,lastname);

			for (i= split ; i < line.length(); i++) {
				if (line.charAt(i) == '"') {
					break;
				} 
			}
			
			// extracts the program list
			plist= line.substring(i+2,line.length()-2);
			String delimiter = ","; // Assuming values are separated by commas
			rol = plist.split(delimiter);
			
			resident.setROL(rol);
			
			residents.put(residentID,resident);
		}	
    }

	// Reads the programs csv file
	// It populates the programs HashMap
    public void readPrograms(String programsFilename) throws IOException, 
													NumberFormatException {

        String line;
		programs= new HashMap<String,Program>();
		BufferedReader br = new BufferedReader(new FileReader(programsFilename)); 

		String programID;
		String name;
		int quota;
		String rlist;
		int[] rol;

		// Read each line from the CSV file
		line = br.readLine(); // skipping first line
		while ((line = br.readLine()) != null && line.length() > 0) {

			int split;
			int i;

			// extracts the program ID
			for (split=0; split < line.length(); split++) {
				if (line.charAt(split) == ',') {
					break;
				} 
			}			
			if (split > line.length()-2)
				throw new IOException("Error: Invalid line format: " + line);


			programID= line.substring(0,split);
			split++;

			// extracts the program name
			for (i= split ; i < line.length(); i++) {
				if (line.charAt(i) == ',') {
					break;
				} 
			}
			if (i > line.length()-2)
				throw new IOException("Error: Invalid line format: " + line);
			
			name= line.substring(split,i);
			split= i+1;
			
			// extracts the program quota
			for (i= split ; i < line.length(); i++) {
				if (line.charAt(i) == ',') {
					break;
				} 
			}
			if (i > line.length()-2)
				throw new IOException("Error: Invalid line format: " + line);

			quota= Integer.parseInt(line.substring(split,i));
			split= i+1;		
				
			Program program= new Program(programID,name,quota);

			for (i= split ; i < line.length(); i++) {
				if (line.charAt(i) == '"') {
					break;
				} 
			}
			
			// extracts the resident list
			rlist= line.substring(i+2,line.length()-2);
			String delimiter = ","; // Assuming values are separated by commas
			String[] rol_string = rlist.split(delimiter);
			rol= new int[rol_string.length];
			for (int j=0; j<rol_string.length; j++) {
				
				rol[j]= Integer.parseInt(rol_string[j]);
			}
			
			program.setROL(rol);
			
			programs.put(programID,program);
		}	
    }
	
	public void galeShapleyAlgorithm(){
		matches = new HashMap<>();
		unmatched = new ArrayList<>();
			while (unmatched.size() + matches.size() != residents.size()){
				for (Resident resident : residents.values()){
					if (resident.getMatchedProgram() == null){
						for (int j = 0; j < resident.getROL().length; j++){
							String programString = resident.getROL()[j];
							Program program = programs.get(programString);
							int leastPreferedID = 0;
							int leastPreferedRank = 0;
							int currentRank = 0;
							if(program.empty() == false){
								leastPreferedID = program.leastPreferred().getID();
								leastPreferedRank = program.rank(leastPreferedID);
								currentRank = program.rank(resident.getID());
							}
							if (program.member(resident.getID()) == false){
								if((resident.getROL().length - 1 == j) && resident.getMatchedProgram() == null && unmatched.contains(resident) == false){
									unmatched.add(resident);
								}
								continue;
							}
							else if(program.full() == false){
								matches.put(resident.getID(), programString);
								program.getMatchedResidents().add(resident);
								resident.setMatchedProgram(program);
								resident.setMatchedRank(program.rank(resident.getID()));
								break;
							}
							else if(currentRank < leastPreferedRank && program.empty() == false){
								Resident worst = program.leastPreferred();
								matches.remove(worst.getID());
								program.getMatchedResidents().remove(worst);
								worst.setMatchedProgram(null);
								worst.setMatchedRank(-1);
								matches.put(resident.getID(), programString);
								program.getMatchedResidents().add(resident);
								resident.setMatchedProgram(program);
								resident.setMatchedRank(program.rank(resident.getID()));
								break;
							}
							if((resident.getROL().length - 1 == j) && resident.getMatchedProgram() == null && unmatched.contains(resident) == false){
									unmatched.add(resident);
							}
						}
					}
				}
			}

	}

	public static void main(String[] args) {
		
		try {
			
			GaleShapley gs= new GaleShapley(args[0],args[1]);

			gs.galeShapleyAlgorithm();
			System.out.println("lastname, firstname, residentID, programID, name");
			for (Map.Entry<Integer, String> match : gs.matches.entrySet()){
				int residentID = match.getKey();
				String residentName = gs.residents.get(residentID).getName();
				String programID = match.getValue();
				String programName = gs.programs.get(programID).getName();
				System.out.println(residentName + ", " + residentID + ", " + programID + ", " + programName);
			}
			for (Resident resident: gs.unmatched){
				int residentID = resident.getID();
				String residentName = resident.getName();
				System.out.println(residentName + ", " + residentID + ", " + "XXX, NOT_MATCHED");
			}
			System.out.println();
			System.out.println("Number of unmatched residents: " + gs.unmatched.size());
			int quotaCount = 0;
			for(Program program : gs.programs.values()){
				quotaCount += program.getQuota();
			}
			System.out.println("Number of positions available: " + (quotaCount - gs.matches.size()));
			
        } catch (Exception e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
	}
}
