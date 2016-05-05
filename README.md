Play Reactive Workshop @ Tieto Networking Conference

Application has 2 web pages:

* Search in GitHub repositories
* Additional functionality to leave comments for particular repository

Presentation slides are stored in `presentation` folder

# Running

Optionally specify GITHUB_TOKEN to have bigger req/min threshold

```
    $ export GITHUB_TOKEN=someToken 
    $ activator
    $ run
```


# Prepare environment

Tooling needed:

 - Git
 - Java 8
 - [IntelliJ  IDEA](https://www.jetbrains.com/idea/download) (community edition is enough)
 - IntelliJ Scala plugin. By default it is bundled together with IntelliJ, if somehow you don't have it IntelliJ will suggest to install it after you open scala file for the first time
 - Lightbend Activator. [download link](https://www.lightbend.com/activator/download),

Check if activator installed:

```
$ activator --version
```

It would be nice if you try to run and browsing sample scala-play-application to do that follow these instructions: 

```
$ activator new

... choose play-scala template

Enter a name for your application (just press enter for 'play-scala')
> hello-scala-play

$ cd hello-scala-play
$ activator run 
```

Open page http://localhost:9000

You should see welcome page.

# Tasks
 Should be performed in this order. Search in code for string `Task: taskName`
 
 - Task: Intro using IntroductionSpec
 - Task: Search
 - Task: Display repository information
 - Task: Create Comment
 - Task: Get comment list
 - Task: Comment Like