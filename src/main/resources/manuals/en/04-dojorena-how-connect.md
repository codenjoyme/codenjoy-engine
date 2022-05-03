## Connect to the server

So, the player registers on the server and joining the game.

Then you should connect from client code to the server via websockets. This [collection of clients](https://github.com/codenjoyme/codenjoy-clients.git) for different programming languages will help you. How to start a client please check at the root of the project in the README.md file.

If you can't find your programming language, you're gonna have to write your client (and then send us to the mail:
[oleksandr_baglai@epam.com](mailto:oleksandr_baglai@epam.com)

Address to connect the game on the server looks like this (you can copy it from your game room):

`https://[server]/codenjoy-contest/board/player/[user]?code=[code]`

Here `[server]` - domain/id of server, `[user]` is your player id and `[code]` is your security token. Make sure you keep the code safe from prying eyes. Any participant, knowing your code, can play on your behalf.