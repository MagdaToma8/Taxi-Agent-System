# Taxi-Agent-System

Η υλοποίηση της παρακάτω εργασίας έγινε με χρήση java και jason.

# Setup Jason

1. Βεβαιωθείτε ότι έχετε εγκαταστήσει το terminal (GitBash)
2. στο SourceForge κατεβάστε το jason-bin-3.2.0 αρχείο. Όταν το κατεβάσετε, αποσυμπιέστε το αρχείο.
3. Ανοίξτε το Visual Studio Code και επιλέξτε ως terminal το GitBash.
4. Στο terminal, γράψτε cd "your_jason_directory\bin" για να πάτε στο Jason directory's bin φάκελλο.
5. Κάντε το jason αρχείο σας Unix εκτελέσιμο με την εντολή : chmod +x jason.
6. Για ευκολία προτείνω να προσθέσετε το your_jason_directory\bin στο system PATH σας. Εδώ  θα βρείτε όλα τα βήματα.

# Δημιούργησε το δικό σου Jason Application 

1. Πήγαινε στο Jason bin directory : cd "your_jason_directory\bin"
2. Δημιούργησε ένα default app με την εντολή : jason app create your_app
3. Πήγαινε στο δικό σου app dirextory: cd your_app
4. Τρέχε το πρόγραμμα σου: jason your_app.mas2j
   
# Πρόβλημα

Στην παρούσα εργασία, έχουμε έναν πράκτορα taxi που μετακινείται σε έναν κόσμο 5x5 και σκοπός του είναι να εξυπηρετήσει τους πελάτες. Υπάρχουν 4 διακριτές θέσεις: η κόκκινη (R), η μπλε (B), η πράσινη (G) και η κίτρινη (Y). Επίσης, υπάρχουν διάφοροι τοίχοι σε αυτόν τον κόσμο, οι οποίοι είναι αρχικά άγνωστοι στον πράκτορα. Οι πελάτες θα εμφανίζονται τυχαία σε μία από τις διακριτές θέσεις και θέλουν να μεταβούν σε μία άλλη. Ο πράκτορας πρέπει να μεταβεί στην θέση του πελάτη, να τον φορτώσει, να τον πάει στην τελική του θέση και να τον εκφορτώσει.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/74f295ba-63b0-4af9-b32e-c1ba85d458fe)

Σε κάθε επεισόδιο ο πράκτορας ξεκινάει από μια τυχαία θέση στον κόσμο. Ένα επεισόδιο ορίζεται από μία μεταβλητή, την maxActions, η οποία καθορίζει πόσες ενέργειες μπορεί να κάνει ο πράκτορας μέχρι να τελειώσει το επεισόδιο. Όταν τελειώσει το επεισόδιο, δηλαδή το maxActions γίνει 0, τότε ο αλγόριθμος ξεκινάει από την αρχή, όλα τα intentions και τα beliefs του πράκτορα διαγράφονται και ο πράκτορας είναι έτοιμος να εξυπηρετήσει τους πελάτες που θα εμφανιστούν.

Ο πράκτορας θα πρέπει να πραγματοποιεί τις ακόλουθες ενέργειες με κόστος -1 για κάθε ενέργεια: (α) φόρτωση πελάτη, (β) εκφόρτωση πελάτη, (γ) κίνηση στο επόμενο τετράγωνο επάνω, (δ) κάτω, (ε) δεξιά, (ζ) αριστερά. Αυτός θα λαμβάνει ανταμοιβή +20 κάθε φορά που εκφορτώνει κάποιον πελάτη επιτυχώς, ενώ έχει ένα κόστος -10 αν προσπαθήσει να φορτώσει ή εκφορτώσει τον πελάτη από/σε λάθος θέση και κόστος -100 αν προσκρούσει σε τοίχο.

# Αρχιτεκτονική Πράκτορα

Beliefs 

• at(O,C,R): Αυτό σημαίνει ότι το αντικείμενο Ο βρίσκεται στην στήλη C και γραμμή R. To O παίρνει τιμές {taxi, client} ενώ τα C και R παίρνουν τιμές στο διάστημα [0, 4].

• drop(client,C,R): Χρησιμοποιούμε αυτό το κατηγόρημα για να υποδηλώσουμε την θέση στην οποία θέλει να πάει ο πελάτης. Ομοίως με παραπάνω τα C και R παίρνουν τιμές στο διάστημα [0, 4].

• serving(taxi): To κατηγόρημα αυτό υποδηλώνει ότι ο πράκτορας taxi εξυπηρετεί κάποιον πελάτη.

• hasReached(maxActions): Το κατηγόρημα αυτό υποδηλώνει ότι ο πράκτορας έχει πραγματοποιήσει τον μέγιστό αριθμό ενεργειών για αυτό το επεισόδιο.

Ενέργειες

• checkForClient: O πράκτορας ελέγχει αν υπάρχει κάποιος πελάτης στο grid. Η ενέργεια αυτή δεν επιφέρει κάποια αλλαγή στο περιβάλλον. Είναι βοηθητική και ο πράκτορας θα κάνει αυτή την ενέργεια μέχρι να εμφανιστεί κάποιος πελάτης.

• chooseClient: O πράκτορας επιλέγει ποιον πελάτη θα εξυπηρετήσει.

• moveTowards(C,R): Ο πράκτορας κινείται προς την θέση (C, R).

• loadClient(C,R): O πράκτορας φορτώνει τον πελάτη στην θέση (C, R).

• unloadClient(C,R): O πράκτορας εκφορτώνει τον πελάτη στην θέση (C, R).


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/714da728-77cc-4c0e-a07b-4526f75ebd12)

Έχοντας ορίσει τα κατηγορήματα και τις ενέργειες μπορούμε να υλοποιήσουμε και τα πλάνα του πράκτορα.

Ο πράκτορας μας ξεκινάει πάντα έχοντας ως στόχο το check. O στόχος αυτός είναι επαναληπτικός. Σε περίπτωση που ο πράκτοράς μας δεν έχει το belief serving(taxi), τότε θα κάνει την ενέργεια checkForClient και αφού την πραγματοποιήσει θα ξαναπροσθέσει τον στόχο check στα intentions του.

Την στιγμή που προστεθεί το belief serving(taxi) και ο πράκτοράς μας δεν έχει το desire(serve(client)) τότε το προσθέτει ως στόχο και είναι έτοιμος να εξυπηρετήσει τον πελάτη.
Το serve(client) ισχύει κάθε φορά που το serving(taxi) είναι true και αποτελείται από τα εξής:

1. chooseClient, όπου ο πράκτορας κάνει την ενέργεια αυτή για να επιλέξει τον πελάτη που θα εξυπηρετήσει.
2. ?at(client,C,R), όπου ο πράκτορας ελέγχει την τοποθεσία στην οποία βρίσκεται ο πελάτης.
3. !at(taxi,C,R), όπου ο πράκτορας θα προσθέσει ως στόχο να βρεθεί στην θέση του πελάτη.
4. loadClient(C,R), όπου ο πράκτορας θα φορτώσει τον πελάτη στην θέση που βρίσκεται.
5. ?drop(client,C1,R1), όπου ο πράκτορας ελέγχει την τοποθεσία στην οποία θέλει να μεταβεί ο πελάτης.
6. !at(taxi,C1,R1), όπου ο πράκτορας θα προσθέσει ως στόχο να βρεθεί στην θέση που θέλει να μεταβεί ο πελάτης.
7. unloadClient(C1,R1), όπου ο πράκτορας θα εκφορτώσει τον πελάτη στην θέση που ήθελε.
8. !serve(client), όπου ο πράκτορας θα προσθέσει το στόχο αυτό για να εξυπηρετήσει κάποιον άλλον πελάτη.
   
Επομένως, το παραπάνω πλάνο γίνεται επαναληπτικά.

Ας δούμε τώρα τον στόχο at(taxi,C,R). Στην περίπτωση που ο πράκτορας έχει το belief ότι είναι στην θέση αυτή τότε ο στόχος εκπληρώνεται. Διαφορετικά κάνει την ενέργεια moveTowards προς το C,R και ξαναπροσθέτει τον στόχο at(taxi,C,R) για να δει αν εκπληρώθηκε.

Τέλος, όταν προστεθεί το belief hasReached(maxActions), τότε ο πράκτορας διαγράφει όλα τα desires και intentions του και προσθέτει τον στόχο check ώστε να ξαναξεκινήσει από την αρχή.

Παρακάτω βλέπουμε μαζεμένα την αρχιτεκτονική του


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/40c99c99-6c41-4851-9508-9a4228e9f760)

Αυτή ήταν λοιπόν η αρχιτεκτονική του πράκτορα. Ας δούμε λίγο σε βάθος τις ενέργειες που κάνει ο πράκτορας και πως λειτουργεί το περιβάλλον.

Αρχικά είναι σημαντικό να αναφέρουμε ότι το grid που χρησιμοποιούμε έχει διαφορετικές συντεταγμένες από αυτό της εκφωνήσης. Για παράδειγμα το σημείο (0,0) είναι η θέση Red στο grid μας, αντί για το yellow. Επομένως για τυχόν παρερμηνείες αντί για (x,y) θα αναφερόμαστε σε (C,R).

Είπαμε ότι κατά την διάρκεια του simulation εμφανίζονται πράκτορες στον κόσμο. Για να μπορεί ο πράκτορας να τους εξυπηρετεί σε έναν καλό βαθμό, θεωρούμε ότι υπάρχουν μόνο δύο πράκτορες την φορά. Ένας πράκτορας όταν εμφανίζεται έχει και ένα ζεύγος από τοποθεσίες, το που βρίσκονται (που θα τους φορτώσει ο πράκτορας) και το που θέλουν να πάνε.

Έχοντας εμφανιστεί τουλάχιστον ένας πελάτης στο σύστημα, ο πράκτορας είναι πλέον έτοιμος να κάνει την ενέργεια chooseClient. Σκοπός του πράκτορα είναι να εξυπηρετήσει εκείνον τον πελάτη που θεωρητικά του δίνει το μικρότερο reward. Για να γίνει αυτό, ο πράκτορας θα χρησιμοποιήσει την απόσταση Manhattan αθροίζοντας την απόσταση του από την θέση του πελάτη, με την απόσταση από την θέση του πελάτη μέχρι την θέση εκφόρτωσης του. Έτσι θα έχει την συνολική απόσταση της διαδρομής. Αφού συγκρίνει όλες τις αποστάσεις, ο πράκτορας επιλέγει να εξυπηρετήσει αυτόν με την μικρότερη απόσταση.

Τώρα ο πράκτορας μας θα κάνει την ενέργεια moveTowards ούτως ώστε να πάει στον πελάτη που επέλεξε. Στόχος του πράκτορα είναι πάντα να πηγαίνει πρώτα στην ίδια στήλη, με αυτήν της θέσης που θέλει να πάει, και μετά να πηγαίνει στην ίδια γραμμή. Για να το κάνει αυτό, έχουμε υλοποιήσει μία βοηθητική συνάρτηση, την moveTo. Σε αυτήν ουσιαστικά βρίσκεται όλη η λογική της κίνησης. Η moveTo παίρνει ως όρισμα την κατεύθυνση στην οποία θέλει να πάει ο πράκτορας, δηλαδή πάνω, κάτω, δεξιά ή αριστερά.

Ο πράκτοράς μας λοιπόν κινείται στο περιβάλλον. Κατά την διάρκεια της κίνησης αυτής, ο πράκτορας μπορεί να έρθει αντιμέτωπος με διάφορες καταστάσεις. 
Παρακάτω φαίνονται οι καταστάσεις στις οποίες μπορεί να πέσει ο πράκτορας.
1. Επιτυχής κίνηση: Στην περίπτωση αυτή ο πράκτορας πραγματοποιεί με επιτυχία την κίνηση, μιας και δεν υπάρχουν εμπόδια.
2. Άγνωστος τοίχος. Ο πράκτορας επιχειρεί να κινηθεί σε ένα μέρος. Ωστόσο, υπάρχει τοίχος εκεί και έτσι η κίνηση δεν μπορεί να πραγματοποιηθεί με επιτυχία. Πλέον ο πράκτορας θα γνωρίζει ότι σε αυτή την τοποθεσία υπάρχει τοίχος.
3. Γνωστός τοίχος: Ο πράκτορας επιθυμώντας να κινηθεί προς μία κατεύθυνση διαπιστώνει πως υπάρχει κάποιος τοίχος που τον εμποδίζει. Η λογική που θα ακολουθήσει είναι η ακόλουθη:
    a. Αφού δεν μπορεί να βρει με την πρώτη προσπάθεια την στήλη της τοποθεσίας στόχου, θα επιχειρήσει να βρει την γραμμή του στόχου. Αφού την βρει, θα ξαναπροσπαθήσει να κινηθεί στην στήλη του στόχου.
   
    b. Στην περίπτωση που βρεθεί στην ίδια γραμμή με τον στόχο, αλλά δεν μπορεί να κινηθεί στην στήλη του στόχου λόγω τοίχου, τότε ο πράκτορας θα επιλέξει τυχαία προς τα που να κινηθεί. Η κίνηση αυτή θα είναι στην ίδια στήλη αλλάζοντας γραμμή, μέχρι να ξεφύγει από τοίχους. Συγκεκριμένα, αν ο πράκτορας βρίσκεται στα όρια του grid, τότε αντί να επιλέξει τυχαία, θα κινηθεί προς την κατεύθυνση που τον απομακρύνει από αυτά.
   
Έτσι λοιπόν ο πράκτορας καταφέρνει να μετακινηθεί στο grid και να αντιμετωπίσει τα εμπόδια που υπάρχουν.

Αφού φτάσει στον πελάτη, μπορεί να τον φορτώσει κάνοντας την ενέργεια loadClient. Έτσι, πλέον δεν είναι διαθέσιμος για άλλους πελάτες και θέτει ως στόχο του να κινηθεί στο σημείο εκφόρτωσης. Μόλις φτάσει εκεί κάνει την unloadClient, καθιστώντας τον έτσι διαθέσιμο να εξυπηρετήσει κάποιον άλλον.

Η διαδικασία αυτή θα γίνεται επαναληπτικά μέχρι να τελειώσει το επεισόδιο, όπου όλα θα ξαναξεκινήσουν από την αρχή, εκτός βέβαια από την γνώση του πράκτορα σχετικά με τους τοίχους του περιβάλλοντος. Έτσι ενώ στην αρχή θα παίρνει ένα πολύ χαμηλό reward, κατά την πάροδο των επεισοδίων το reward θα αυξάνεται.

Παρακάτω βλέπουμε δύο γραφήματα με το πως κινείται το reward που έχει πάρει ο πράκτορας κατά την πάροδο 6 επεισοδίων. Στο πρώτο γράφημα το επεισόδιο ήταν 30 ενεργειών, ενώ στο δεύτερο ήταν 40 ενεργειών. Για να έχουμε μια πληρέστερη εικόνα τρέξαμε το πρόγραμμα δύο φορές.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/15e55a3c-dcbd-42ef-a290-53f2ffeae7f7)


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/4c66f84b-b8ff-4063-8cc1-b3369bdae11d)

Βλέπουμε λοιπόν ότι το reward στα πρώτα επεισόδια είναι αρκετά χαμηλό, όπως ήταν αναμενόμενο, και ότι καθώς περνάνε τα επεισόδια, το reward αυξάνεται σημαντικά μιας και ο πράκτορας πλέον γνωρίζει τους τοίχους στο περιβάλλον. Επίσης μπορούμε να συμπεράνουμε ότι όσο περισσότερες ενέργειες μπορεί να κάνει ένας πράκτορας ανά επεισόδιο, τόσο πιο γρήγορα αυξάνεται το reward. Για παράδειγμα εδώ, βλέπουμε ότι όταν το επεισόδιο αποτελούνταν από 30 ενέργειες, ο πράκτορας πήρε θετικό reward στο 6ο επεισόδιο. Από την άλλη, όταν το επεισόδιο ήταν 40 ενεργειών, ο πράκτορας σίγουρα θα ‘χει πάρει θετικό reward στο 4ο επεισόδιο.

# Παράδειγμα Εκτέλεσης

Ο αλγόριθμος μας ξεκινάει και τοποθετεί τον πράκτορα taxi σε μία τυχαία θέση στο grid:


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/e5cd54e8-a6a3-43aa-ad0c-1ae4c9b95ab9)

Στην παραπάνω περίπτωση, ο πράκτορας βρίσκεται στην θέση (2,3).


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/c527b58a-4b3a-49f6-87b3-492dd7f27fcb)

Ο αλγόριθμος ελέγχει στην αρχή αν υπάρχει κάποιος πελάτης. Στην περίπτωση που δεν υπάρχει, περιμένει στο σημείο που βρίσκεται και ξανά προσπαθεί. Έπειτα, παρατηρούμε ότι εμφανίστηκε ένας πελάτη και από την στιγμή που δεν υπάρχει άλλος πελάτης, επιλέγει αυτόν. Ο πελάτης μας θέλει να πάει από την θέση (3,4), δηλαδή από την θέση μπλε, στην θέση (4,0), δηλαδή στην πράσινη.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/86d51a93-1b52-4be7-b700-e0ee7b68c7e8)

Ο πράκτορας προσπαθεί να πάει στην θέση «μπλε» (moveTowards(3,4)) και από την θέση (2,3) που βρισκόταν να κάνει δεξιά, για να βρεθεί στην ίδια στήλη με τον πελάτη. Όμως, βρίσκει σε τοίχο και το reward του γίνεται -100. Ο πράκτορας μετά, για να αποφύγει τον τοίχο θα προσπαθήσει να πάει στην ίδια γραμμή με τον πελάτη. Επομένως θα πάει κάτω και το reward τώρα είναι -101. Έχοντας βρεθεί στην ίδια γραμμή θα ξαναεπιχειρήσει να πάει στην ίδια στήλη με τον πελάτη. Όμως, αποτυγχάνει ξανά καθώς βρίσκει τοίχο και το reward γίνεται -201.

Παράλληλα έχει δημιουργηθεί ένας νέος πελάτης στην θέση «κόκκινο».


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/d26f6dca-5a6a-49e8-9600-e99caf426fab)


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/c7ed7e18-8326-4ae3-80d0-c9c89eda6c7d)

Εφόσον ο πράκτορας βρήκε την γραμμή που βρίσκεται ο πελάτης και από την στιγμή που βρίσκεται στα όρια του grid δεν έχει άλλη επιλογή από το να πάει πάνω και το reward γίνεται -202.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/57925b1e-b7fe-4889-93da-d28322dee954)


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/6e2f2b7d-d998-4e5c-9f2c-8267d8b8c105)

Από την στιγμή που ο πράκτορας βρήκε προηγουμένως τοίχο δεξιά από το σημείο (2,3), δεν θα ξανά προσπαθήσει να κάνει δεξιά και θα κινηθεί πάνω, όπως και το έχει αποφασίσει από το προηγούμενο βήμα.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/7519d947-8da9-4b99-b28b-1b370c7ea132)

Τώρα βρίσκεται στην θέση (2,2) και το reward είναι -203.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/fb8a35c8-1218-4215-803f-2ee755b2d9bd)


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/9e7341a2-7568-4cd7-9956-cb8f6a8d72ea)

Ο πράκτορας τώρα κάνει δεξιά, καθώς δεν βρέθηκε τοίχος σε αυτό το σημείο και βρίσκεται στην ίδια στήλη με τον πελάτη, στην θέση (3,2) με reward -204.

Από την στιγμή που ο πράκτορας βρήκε την ίδια στήλη, πάει να ξανά βρει την ίδια γραμμή πηγαίνοντας κάτω.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/e3d678f1-dcd1-42a9-b708-2c72b073af37)


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/fc2c7c3e-8e00-4c04-9687-5f719141a80a)


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/0073c413-e9bd-4c46-8739-e93fbad9a44a)


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/3a836fc6-1146-4e6b-be7a-910c9b1ec65a)

Ο πράκτορας τώρα βρίσκεται στην ίδια θέση με τον πελάτη και τον φορτώνει.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/3ea42ea9-8c56-424b-a521-04c950f09eff)


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/c9fa9c4d-1743-44a7-82c1-cbff09184c15)

Ο πράκτορας τώρα καλεί την moveTowards(4,0) για να πάει στην dropLocation του πελάτη. Κινείται προς τα δεξιά και βρίσκει την ίδια στήλη.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/d56ecb14-2573-41eb-8a32-7bbb5563ba63)

Ακολουθείται η ίδια διαδικασία, ώσπου ο πράκτορας βρίσκεται στο σημείο που πρέπει να κάνει unload τον πελάτη και παίρνει reward +20.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/bcb4116c-ae9d-494f-9cc7-86d37a09f3df)

Μετά από 20 κινήσεις (maxActions = 0) τελειώνει το επεισόδιο με τελικό reward=-194 και ο πράκτορας εμφανίζεται σε ένα τυχαίο σημείο στο grid και περιμένει τους πελάτες να εμφανιστούν.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/0d11ef77-443d-422b-9622-493581ed96ca)

Ο πράκτορας στο νέο επεισόδιο θα θυμάται τους τοίχους που προσέκρουσε στο προηγούμενο επεισόδιο και δεν θα ξανά πέσει πάνω τους. Το total reward θα μικραίνει σταδιακά, καθώς περνάνε τα επεισόδια, γιατί ο πράκτορας θα έχει βρει όλους τους τοίχους με αποτέλεσμα να μην ξανά πάρει reward -100 και να φτάνει πιο γρήγορα στο dropLocation του πελάτη, παίρνοντας έτσι πιο συχνά το reward +20.

Στο παρακάτω παράδειγμα, υπάρχουν 2 πελάτες που περιμένουν να εξυπηρετηθούν. Ο πρώτος πελάτης βρίσκεται στην κίτρινη θέση και θέλει να πάει στην κίτρινη θέση ενώ ο δεύτερος πελάτης βρίσκεται στην πράσινη θέση και θέλει να πάει στην μπλε. Ο πράκτορας μας βρίσκεται στην κίτρινη θέση και για να αποφασίσει ποιόν πελάτη θα εξυπηρετήσει κάνει την chooseClient, η οποία υπολογίζει το Manhattan distance των θέσεων των δύο πελατών σε σύγκριση με την θέση του πράκτορα. Το total distance του πελάτη με id: 1 είναι 13, ενώ του πελάτη με id: 0 είναι 0. Εφόσον ψάχνουμε τον πελάτη με το μικρότερο total distance ο πράκτορας θα επιλέξει να εξυπηρετήσει τον πελάτη με id: 0.


![image](https://github.com/MagdaToma8/Taxi-Agent-System/assets/128919446/a1650ce8-716b-49d3-9972-48a88e2fd570)


