# Harbor House Hostel Management System

A Java Swing desktop application demonstrating MVC-style separation, the Collections Framework, and practical HashMap lookups.

## Features

- Room allocation with capacity validation and checkout
- Complaint tracking with open and resolved statuses
- Fee ledger with pending and paid records
- HashMaps for students, rooms, complaints, and fees
- Automatic local persistence to `hostel-data.ser`
- Sign in, account creation, password visibility controls, and password reset
- User database stored in `users.db` with salted PBKDF2 password hashes
- Sample data on first launch so the interface is immediately usable

## Run

Requires JDK 17 or newer.

```powershell
New-Item -ItemType Directory -Force out
javac -d out src\hostel\*.java
java -cp out hostel.HostelManagementApp
```

The data file is created beside the command when the application first saves a change.

The sign-up screen accepts an optional Google ID/email for account linking. It does not ask for or store a Google password. Real Google authentication requires an OAuth client configured with Google Cloud; never collect Google passwords inside this application.

Supabase connection details are kept locally in `supabase.properties`. This file is ignored by Git and must not be committed.

## Project structure

- `Student`, `Room`, `Complaint`, `FeeRecord`: serializable model entities
- `HostelService`: HashMap-backed business logic and persistence
- `HostelManagementApp`: Swing view and event handlers