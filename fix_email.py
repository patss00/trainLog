import subprocess

old_emails = [
    b"patricia.silva@brightenconsulting.com",
    b"Patricia.Silva@brightenconsulting.com",  # add any variations
]
new_name = b"Patricia Sarreira Silva"
new_email = b"patricia.sarreira.silva@gmail.com"

# This will rewrite all commits in the repo using git-filter-repo
for old_email in old_emails:
    subprocess.run([
        "python", "-m", "git_filter_repo",
        "--force",
        "--replace-text", "-",  # read replacement from stdin
    ], input=f"{old_email.decode()}=={new_name.decode()} <{new_email.decode()}>\n".encode())
