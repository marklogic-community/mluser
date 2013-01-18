MLUser is a Java application for adding users, deleting users, and changing 
users passwords on multiple, unclustered MarkLogic instances. 

To use, add the servers you want to work with to your list of known servers 
(this list will be remembered). For a specific user, add the target servers 
from the known servers list. Select the desired action and hit the Go button. 

When adding a server to the known servers list, you will need to specify an 
XDBC port. Doesn't matter what DB it's connected to, the connection string 
will target the Security database. 