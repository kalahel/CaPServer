# Workshop chipset
## Authentication
If the username is wrong send an selX, selY and a seed anyway to prevent easy guessing of username.

`HMAC = md5(x # md5(y # pwd))`