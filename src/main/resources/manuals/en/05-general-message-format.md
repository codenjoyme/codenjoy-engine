## Message format

After connection, the client will regularly (every second) receive 
a line of characters with the encoded state of the field. The format:

`^board=(.*)$`

You can use this regular expression to extract a board from
the resulting string.