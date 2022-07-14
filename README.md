# 3D-shooting-game

Read on below for project specifics.

1. [Game Design Document](#game-design-document-sparkling_heart)
2. [Credits](#credits)
3. [Project Comments](#project-comments)
4. [Other](#other)
5. [Project Kanban Board](https://github.com/Slideshow776/Pride-Art-2022/projects/1)

## Game Design Document :sparkling_heart:

1. :blue_heart: Overall Vision 
    * **Write a short paragraph explaining the game:**
            
    * **Describe the genre:**
        
    * **What is the target audience?**
        
    * **Why play this game?**
    
2. :purple_heart: Mechanics: the rules of the game world
    * **What are the character's goals?**
           
    * **What abilities does the character have?**
        
    * **What obstacles or difficulties will the character face?**
        
    * **What items can the character obtain**
        
    * **What resources must be managed?**  
        
 3. :heart: Dynamics: the interaction between the player and the game mechanics
    * **What hardware is required by the game?** 

        * Desktop needs to have a functional mouse, keyboard, and screen. This game will not require a powerful computer.
        
    * **What type of proficiency will the player need to develop to become proficient at the game?**
       
    * **What gameplay data is displayed during the game?**
    
    * **What menus, screens, or overlays will there be?**
   
    * **How does the player interact with the game at the software level?**
    
4. :green_heart: Aesthetics: the visual, audio, narrative, and psychological aspects of the game
    * **Describe the style and feel of the game.**
   
    * **Does the game use pixel art, line art, or realistic graphics?**
        
    * **What style of background music, ambient sounds will the game use?**
           
    * **What is the relevant backstory for the game?** 
        
    * **What emotional state(s) does the game try to provoke?**
       
    * **What makes the game fun?**
        
5. :yellow_heart: Development 
    
    * **List the team members and their roles, responsibilities, and skills.**  
      
        This project will be completed individually; graphics and audio will be obtained from third-party websites that make their assets available under the Creative Commons license, and so the main task will be programming and creating some graphics.
    
    * **What equipment is needed for this project?**   

        A computer (with keyboard, mouse, and speakers) and internet access will be necessary to complete this project.
    
    * **What are the tasks that need to be accomplished to create this game?**    
        
        This project will use a simple Kanban board hosted on the project's GitHub page.
        The main sequence of steps to complete this project is as follows:    
        * Setting up a project scaffold
        * **Programming game mechanics and UI**
        * **Creating and obtaining graphical assets**
        * Obtaining audio assets
        * Controller support
        * **Polishing**
        * Deployment

    * **What points in the development process are suitable for playtesting?**    
        
        The main points for playtesting are when the basic game mechanics of the level screen are implemented, and when it is visualised. The questions that will be asked are:         
        * Is the gameplay and UI understandable?
        * Is the gameplay interesting?
        * How do the controls feel?
        * How is the pace of the game?
        * Are there any improvement suggestions?        
    
    * **What are the plans for publication?**

## Credits

## Project comments
### Wall clipping
To stop the wall from clipping out of sight it is nessasary to set the `camera.near` to a very tiny amount, like so:
```
camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
camera.lookAt(0, 0, 0);
camera.near = .01f;
camera.far = 100f;
camera.update();
```
### Player movement
The cursor can be locked to the window like this: 
`Gdx.input.setCursorCatched(true);`

Keyboard movement is as simple as this: 
```
private void keyboardPolling(float dt) {
   if (Gdx.input.isKeyPressed(Input.Keys.W))
      stage.moveCameraForward(speed * dt);
   if (Gdx.input.isKeyPressed(Input.Keys.A))
      stage.moveCameraRight(speed * dt);
   if (Gdx.input.isKeyPressed(Input.Keys.S))
      stage.moveCameraForward(-speed * dt);
   if (Gdx.input.isKeyPressed(Input.Keys.D))
      stage.moveCameraRight(-speed * dt);
}
```

Mouse movement is this simple line:
```
private void mousePolling() {
   stage.turnCamera(rotateSpeed * Gdx.input.getDeltaX() * BaseGame.mouseMovementSensitivity);
}
```

## Other
For other project specifics check out the [commits](https://github.com/Slideshow776/3D-shooting-game/commits/master).

[Go back to the top](#3d-shooting-game).
