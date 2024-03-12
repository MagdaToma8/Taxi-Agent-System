/* Plans */
!check.

// The agent checks for clients doing the action checkForClient and continues to check recursively
+!check : not serving(taxi)
     <-  checkForClient;
         !check.
+!check.

// A client has appeared and if the agent does not desire to serve the client, it initiates the action to serve the client
+serving(taxi) : not .desire(serve(client))
   <- !serve(client).

// Defines the process to serve a client
+!serve(client) : serving(taxi)
   <- chooseClient; //choose which client is more optimal to serve
      ?at(client,C,R); // find out the location of the chose client   
      !at(taxi,C,R); //go to that location
      loadClient(C,R); //load the client on that location
      ?drop(client,C1,R1); // find out the location in which the client wants to go
      !at(taxi,C1,R1);   //go to that location
      unloadClient(C1,R1); //unload the client on that location
      !serve(client). //serve another client
+!serve(client) <- !check.


// Manages the movement of taxi toward a specific location (C,R).
+!at(taxi,C,R) : at(taxi,C,R).
+!at(taxi,C,R)
    <- moveTowards(C,R); // Repeatedly moving one step towards the right location.
       !at(taxi,C,R).

// If agent has this belief, it drops all of his desires and intentions in order to start again
+hasReached(maxActions) : true
   <- .print("I have reached max Actions"); 
      .drop_all_desires; 
      .drop_all_intentions;
      !check.
