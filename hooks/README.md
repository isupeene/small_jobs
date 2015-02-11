# hooks
Some git hooks that are useful for developers working on this repo.
Copy these into your `.git/hooks/` folder, and make sure they are _executable_.

## post-merge
After pulling from the remote, it's necessary to re-sync the shared code
between the database and the server.  This code is kept in the repository in
the top-level `shared` folder, but is needed in the `database` and `server` folders
as well.  This hook will make sure the code is up-to-date whenever you pull.

