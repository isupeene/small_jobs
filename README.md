# small jobs
Connects contractors with small jobs.

# Project Structure
The project is organized into several subdirectories, organized by component.  Each subfolder also has a README explaining its purpose in more detail.
## database-setup
All scripts for setting up the database are here.  Django requires that the database and user already exist before the server can run, so this directory provides scripts for easily creating the user and database.
## web
This folder contains the Django project, which includes the database schema, the web API, and the Job Posting App.
## Android
This folder contains all the code for the Android Job Seeking App.

# Instructions

## Setup
1. Run **install-deps.sh**  

2. Run **database-setup/ops/create_db.sh**  

3. In the web folder, sync the database using **“python manage.py syncdb”** 

4. Run the server using **“python manage.py runserver 0.0.0.0:8000”**  


## Usage
Before compiling and running the Job Seeker app, in **Android/JobSeeker/src/com/smalljobs/jobseeker/models/Server**, change the ip address to that of the computer running the server.  

Access the job posting website at **localhost:8000/job_posting/homepage/**

Access the admin website at **localhost:8000/admin**  

