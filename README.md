## Coffee Chat
A multi-threaded Java chat application for COMP1549.
- Built in Java
- Supports multiple clients to one server
- Dynamic server hosting (if host leaves, a new one will take over)
- Supports custom commands:
- /whisper [user] [message] - Send a private message to a user
- /kick [user] [reason] - Kick a user from the server
- /list - List all users in the server inc. IP & Ports
- /leader - View the current leader of the server
- /exit - Close the server & client

## Breif Requirements
- GUI Based Program 
- Provide ID (we used username)
- Provide IP
- First member becomes server owner
- The coordinator maintains state of active group members by checking periodically (i.e., say every 20 seconds)
- Any member can request details of existing members from the coordinator and will receive everyone's IDs, IP addresses and ports including the current group coordinator.
- Ability to send public and private messages
- If server owner leaves, a new one will be appointed.
- System should print out messages sent to/by members
- Implementation can run manually or automatically.
- Can disconnect from the server using the GUI.


## Folder Structure

The workspace's main files are under the `src` folder which contains all of the `.java` files as well as the stylised image files for the background and loading interfaces.