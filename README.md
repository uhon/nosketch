# Not Only Sketch - nosketch.com

The super awesome sketching app

Now viewer in webgl

## How to run?

Nosketch uses node modules which have to be compiled into a native javascript-source file which is linked into nosketch-dependencies.
Before first launch or after adding new node dependencies (in lib.js), they have to be generated like this:
```bash
cd bundle && npm install && cd ..
sbt "project bundle" bundle
```

Produced script gets written into: webworker/src/main/resources

For now, DEV-purposes, app is started with a bash alias in .bash_aliases

```bash
alias nosketch="cd ~/workspace/nosketch.com/ && export SBT_OPTS=\"-Xdebug -Xrunjdwp:transport=dt_socket,server=y    ,suspend=n,address=5555\" && sbt -Dsbt.global.base=project/.sbtboot -Dsbt.boot.directory=project/.boot -Dsbt.ivy    .home=project/.ivy \"~run 5000\""
```

after that, just run
```bash
nosketch
```