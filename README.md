wearsendmessage
===============

android wear send message from mobile to wear and the other direction


this project is originally created on android studio 0.84, it was updated to android studio 1.0.  

steps to do migration from android studio 0.84 to 1.0:

1. first, you will see errors like:

Error:(16, 0) Gradle DSL method not found: 'runProguard()' Possible causes:
<ul>
  <li>The project 'shakedetect' may be using a version of Gradle that does not contain the method. <a href="openGradleSettings">Gradle settings</a></li>
  <li>The build file may be missing a Gradle plugin. <a href="apply.gradle.plugin">Apply Gradle plugin</a></li>
</ul>

solution:
to fix this, change *runProguard* in build.gradle to *minifyEnabled*

clean and rebuild

see http://stackoverflow.com/questions/27078075/gradle-dsl-method-not-found-runproguard


2. after the above is done, you may see errors like: 

Build failed with an exception.  * What went wrong: Task '' not found in root project 

solution: 
invalidate cache and restart.
> File | Invalidate Caches / Restart

see http://stackoverflow.com/questions/25172006/android-studio-build-fails-with-task-not-found-in-root-project-myproject  for other things to check

