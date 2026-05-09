% Finds the rank of a resident in the preference list of a program
rankInProgram(ResidentID,ProgramID,Rank) :-
    program(ProgramID,_,_,List),
    nth1(Rank,List,ResidentID).

% Finds the least preferered resident in the match list of a program and their rank given the program and list of residents
leastPreferred(ProgramID,[ResidentID],Resident,RankOfThisResident) :-
    rankInProgram(ResidentID,ProgramID,RankOfThisResident).
leastPreferred(ProgramID,[ResidentID|ResidentRest],LeastPreferredResidentID,RankOfThisResident) :-
    leastPreferred(ProgramID,ResidentRest,LeastPreferredResidentIDRest,RankOfThisResidentRest),
    rankInProgram(ResidentID,ProgramID,ResidentRank),
    ResidentRank > RankOfThisResidentRest,!,
    LeastPreferredResidentID = ResidentID,
    RankOfThisResident = ResidentRank.
leastPreferred(ProgramID,[ResidentID|ResidentRest],LeastPreferredResidentID,RankOfThisResident) :-
    leastPreferred(ProgramID,ResidentRest,LeastPreferredResidentIDRest,RankOfThisResidentRest),
    rankInProgram(ResidentID,ProgramID,ResidentRank),
    LeastPreferredResidentID = LeastPreferredResidentIDRest,
    RankOfThisResident = RankOfThisResidentRest.

% Finds if a resident is currently matched to a program
matched(ResidentID,ProgramID,Ms) :-
    member(match(ProgramID,List),Ms),
    member(ResidentID,List).


% Tries to match a resident to a program from their preference list
offer(ResidentID,CurrentMatchset,NewMatchset) :-
    \+ matched(ResidentID,_,CurrentMatchset),
    resident(ResidentID,_,List),
    member(ProgramID,List),
    offerOne(ResidentID,ProgramID,CurrentMatchset,NewMatchset),!.
offer(_,M,M).

% Tries to match a single resident with a single program
offerOne(ResidentID,ProgramID,CurrentMatchset,NewMatchset) :-
    rankInProgram(ResidentID,ProgramID,_),
    program(ProgramID,_,Capacity,_),
    member(match(ProgramID,List),CurrentMatchset),
    length(List,Length),
    Length < Capacity,
    select(match(ProgramID,List),CurrentMatchset,Rest),
    NewMatchset = [match(ProgramID,[ResidentID|List])|Rest],!.
offerOne(ResidentID,ProgramID,CurrentMatchset,NewMatchset) :-
    rankInProgram(ResidentID,ProgramID,Rank),
    member(match(ProgramID,List),CurrentMatchset),
    leastPreferred(ProgramID,List,LeastPreferredResidentID,RankOfThisResident),
    Rank < RankOfThisResident,
    select(match(ProgramID,List),CurrentMatchset,Rest),
    select(LeastPreferredResidentID,List,ListRest),
    NewMatchset = [match(ProgramID,[ResidentID|ListRest])|Rest].

% Processes 1 round of matching given the list of residents
process_resident_list([],CurrentMatchset,CurrentMatchset).
process_resident_list([ResidentID|ResidentIDs],CurrentMatchset,NewNewMatchset):-
    offer(ResidentID,CurrentMatchset,NewMatchset),
    process_resident_list(ResidentIDs,NewMatchset,NewNewMatchset).

% Creates the list of residents and processes 1 round of matching
process_residents(CurrentMatchset, NewMatchset) :-
    findall(ResidentID, resident(ResidentID,_,_), ResidentIDs),
    process_resident_list(ResidentIDs, CurrentMatchset, NewMatchset).

% Processes rounds of matching until all residents been matched or cannot be matched
process_until_complete(Matchset, Matchset) :-
    process_residents(Matchset,NewMatchset),
    Matchset==NewMatchset,!.
process_until_complete(Matchset,NewNewMatchset):-
    process_residents(Matchset,NewMatchset),
    process_until_complete(NewMatchset,NewNewMatchset).

% Creates the initial matchset, processes the matching and prints the matchset.
gale_shapley :-
    findall(match(P,[]), program(P,_,_,_), Matchset),
    process_until_complete(Matchset, NewMatchset),
    write(NewMatchset).
