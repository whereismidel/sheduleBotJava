# Schedule bot

Required environment variables: `BOT_NAME`, `BOT_TOKEN`, `CLIENT_SECRET`

## Build locally

Build:
```bash
$ docker build -t schedulebot . -f docker/app.Dockerfile
```

Run:
```bash
$ docker run -it schedulebot
```

## Run from Docker Packages

```bash
$ docker run -it ghcr.io/whereismidel/shedulebotjava:main
```
