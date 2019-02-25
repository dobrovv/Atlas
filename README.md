# Atlas
Atlas Tracker
1) Cloning locally,
Go to terminal
Choose directory to save file (use cd to change directory, dir "windows" OR ls "Mac OS" to show what is in folder
Use '''' git clone https://github.com/...... ''''
Process for Repo
After doing some code....

Go to terminal
Locate folder for which clone was made
Use '''' git status '''' to check how many files differ/changed
Use '''' git init '''' to check which repo u r at
Use '''' git branch '''' to know which branch u r at, DONT USE MASTER to add files
Use '''' git push origin master '''' if u want to update your codes to what exists and you haven't done any coding yet since cloning!
Else, follow the following process to add files, commit , push into git and Pull Request to be merged!
Then follow the process in the terminal local directory

Create new branch locally
Branch name should "feature/" followed by a condensed version of the task name
Use git checkout -b branch_name to create branch locally
Commit changes to branch
git add name_of_file_to_add to add file (* for all files)
git commit -m "commit_message" to commit changes
commit_message should start with the task number in square brackets
Push changes to branch
push remote origin branch_name
git push origin
Pull request
When the task is finished, open a pull request on the branch
Set someone as a reviewer
Once the review is complete, merge into dev.
