Running the project with its DB:
  On a terminal, standing on the root of the project, run the command: docker-compose up
  Then run the app. It will connect to the database and create the needed tables
  
Layout of the project:

  Create a directory for an entire model, for example:
    If the model is the User, create a directory with the name 'user'
    Inside it, create the controller, service, repository and model for user.
    
    Repeat this process for every model (which requires a controller, service, repository. 
    If those 3 ain't required, the include the model inside the directory which will use said model)
