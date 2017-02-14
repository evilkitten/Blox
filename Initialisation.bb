Function Initialise()
	SeedRnd MilliSecs()
	AppTitle"Blox","Thank you for playing Blox! Are you sure you wish to exit the game?"
	
	InitialiseControl
	InitialiseData
	InitialiseLogFile
	InitialiseCredits
	InitialiseCamera
	InitialiseSounds
	InitialiseShineTexture
	InitialiseShadowTexture
	InitialiseBat
	InitialiseBall 
	InitialiseBackground
	InitialiseBricks
	InitialiseAliens
	InitialiseBoundaries
	InitialisePhysics
	InitialiseUI
	InitialiseHUD
	InitialiseSpecialMasterMeshes
	InitialiseState
	
	WriteLog("Main Initialisation complete The session seed is 0x"+Hex(RndSeed()))
	
	WriteLog("Runtime "+SystemProperty("appdir"))
	WriteLog("Configuration: "+CONFIGPATH)
	WriteLog("Data library: "+DATAPATH)
	
	If (ChannelPlaying(CHN_THEME)) Then WriteLog("Theme Initialised")
End Function

Function InitialiseCamera()
	Graphics3D GWIDTH,GHEIGHT,GDEPTH,2
	SetBuffer BackBuffer()
	
	AmbientLight AMBIENT,AMBIENT,AMBIENT
	SUN=CreateLight()
	PositionEntity SUN,SUN_X,SUN_Y,SUN_Z,True
	RotateEntity SUN,SUN_PITCH,SUN_YAW,0,True
	
	CAMERA=CreateCamera()
	MoveEntity CAMERA,CAMERA_XOFFSET,CAMERA_HEIGHT,GENERIC_Z_OFFSET-CAMERA_DISTANCE
	
	HidePointer
End Function

Function InitialiseControl()
	;CURRENT_CONTROL_METHOD=CONTROL_DEFAULT_METHOD
End Function

Function InitialiseData()
	InitialiseDefaultConfigs
	
	DATAPATH=Replace(GetEnv("LOCALAPPDATA")+"\","\\","\")
	If (FileType(DATAPATH)<>2)
		RuntimeError("Invalid ENV_VAR path: "+GetEnv("LOCALAPPDATA"))
	End If
	
	DATAPATH=DATAPATH+"EKD\"
	If (FileType(DATAPATH)<>2)
		CreateDir(DATAPATH)
	End If
	If (FileType(DATAPATH)<>2)
		RuntimeError("Cannot access path: "+DATAPATH)
	End If
	
	DATAPATH=DATAPATH+"Blox\"
	If (FileType(DATAPATH)<>2)
		CreateDir(DATAPATH)
	End If
	If (FileType(DATAPATH)<>2)
		RuntimeError("Cannot access path: "+DATAPATH)
	End If
	
	LOGPATH=DATAPATH+LOGFILE
	
	CONFIGPATH=DATAPATH+CONFIGFILE
	
	DATAPATH=DATAPATH+DATAFILE
	
	If (FileType(CONFIGPATH)<>1)
		DebugLog("Cannot read configfile. Setting default values")
		WriteConfig
	End If
	
	If (FileType(DATAPATH)<>1)
		DebugLog("Cannot read datafile. Setting default values")
		InitialiseDefaultData
		WriteData
	End If
	
	ReadConfig
	ReadData
End Function

Function InitialiseCredits()
	PopulateCredits
End Function

Function InitialiseUI()
	DefineMenuOptions
	UI_CURRENT_MENUOPTION=First OPTIONS
End Function

Function InitialiseDefaultData()
	HISCOREINITS=DEF_INITS
	HISCORE=DEF_HISCORE
	HIGHEST_STAGE_REACHED=DEF_HIGHEST_STAGE
End Function

Function InitialiseDefaultConfigs()
	CURRENT_CONTROL_METHOD=CONTROL_DEFAULT_METHOD
	
	CTRL_KEY_LEFT=KEY_LEFT
	CTRL_KEY_RIGHT=KEY_RIGHT
	CTRL_KEY_LAUNCH=KEY_SPACE
	
	CTRL_JOY_LAUNCH=JOY_DEFAULT_LAUNCH
	
	JOY_PORT=JOY_DEFAULT_PORT
	
	JOY_DEADZONE=JOY_DEFAULT_DEADZONE
	
	AUDIO_MUS_VOL#=AUDIO_DEFAULT_MUS_VOL#
End Function

Function InitialiseSounds()
	SND_BALL_BOUNCE_BAT=LoadSound(SOUNDPATH+"bouncebat.ogg")
	SND_BALL_BOUNCE_BOUND=LoadSound(SOUNDPATH+"bouncewall.ogg")
	SND_BAT_BOUND=LoadSound(SOUNDPATH+"batwall.ogg")
	SND_ALIEN_SPAWN=LoadSound(SOUNDPATH+"alienspawn.ogg")
	SND_ALIEN_DESTROY=LoadSound(SOUNDPATH+"aliendestroy.ogg")
;	;SND_BRICK_DAMAGE=LoadSound(SOUNDPATH+"brickdamage.ogg")
	SND_BRICK_DESTROY=LoadSound(SOUNDPATH+"brickdestroy.ogg")
	SND_BRICK_INVINCIBLE=LoadSound(SOUNDPATH+"brickinvincible.ogg")
	SND_BRICK_IMPOSSIBLE_TIMEOUT=LoadSound(SOUNDPATH+"invincibledestroy.ogg")
	SND_PLAYER_LIFELOST=LoadSound(SOUNDPATH+"lifelost.ogg")
	SND_PLAYER_LIFEGAINED=LoadSound(SOUNDPATH+"lifegained.ogg")
	
	SND_SPECIAL_EXTEND=LoadSound(SOUNDPATH+"specialextend.ogg")
	SND_SPECIAL_FADE=LoadSound(SOUNDPATH+"specialfade.ogg")
	SND_SPECIAL_SLOW=LoadSound(SOUNDPATH+"specialslow.ogg")
	SND_SPECIAL_REDUCE=LoadSound(SOUNDPATH+"specialreduce.ogg")
	SND_SPECIAL_MULTIBALL=LoadSound(SOUNDPATH+"specialmulti.ogg")
	SND_SPECIAL_SCORE=LoadSound(SOUNDPATH+"specialscore.ogg")
	
End Function

Function InitialiseBackground()
	BACKDROP=CreatePlane(POLYCOUNT_DETAIL)
	
	RotateEntity BACKDROP,CYLINDER_ALIGNMENT_ANGLE,0,180,True
	
	PositionEntity BACKDROP,0,0,GENERIC_Z_OFFSET+BACKDROP_DISTANCE_OFFSET,True
	EntityFX BACKDROP,1
	
;	MIRROR=CreateMirror()
;	RotateEntity MIRROR,0-CYLINDER_ALIGNMENT_ANGLE,0,0
;	PositionEntity MIRROR,0,0,GENERIC_Z_OFFSET+BACKDROP_DISTANCE_OFFSET+1,True
;	HideEntity MIRROR
	
End Function

Function InitialiseShineTexture()
	;SHINETEXTURE=LoadTexture(GRAPHICSPATH+SHINETEXTURENAME,SHINETEXTUREFLAGS)
	
	SHINETEXTURE=CreateTexture(SHINETEXTURE_SIZE,SHINETEXTURE_SIZE,SHINETEXTUREFLAGS)
	Restore SSD1_
	Local X
	Local Y
	Local Pixel
	Local Buffer=TextureBuffer(SHINETEXTURE)
	LockBuffer Buffer
	For Y=0 To SHINETEXTURE_SIZE-1
		For X=0 To SHINETEXTURE_SIZE-1
			Read Pixel
			WritePixelFast X,Y,Pixel,Buffer
		Next
	Next
	
	UnlockBuffer Buffer
End Function

Function InitialiseShadowTexture()
	;SHADOWTEXTURE=LoadTexture(GRAPHICSPATH+SHADOWTEXTURENAME,SHADOWTEXTUREFLAGS)
	SHADOWTEXTURE=CreateTexture(SHADOWTEXTURE_SIZE,SHADOWTEXTURE_SIZE,SHADOWTEXTUREFLAGS)
	Restore SSD2_
	Local X
	Local Y
	Local Pixel
	Local Buffer=TextureBuffer(SHADOWTEXTURE)
	LockBuffer Buffer
	For Y=0 To SHADOWTEXTURE_SIZE-1
		For X=0 To SHADOWTEXTURE_SIZE-1
			Read Pixel
			WritePixelFast X,Y,Pixel,Buffer
		Next
	Next
	
	UnlockBuffer Buffer	
End Function

Function InitialiseBall()
;	BALL=CreateSphere(POLYCOUNT_DETAIL)
;	ScaleMesh BALL,BALL_SIZE,BALL_SIZE,BALL_SIZE
;	EntityColor BALL,BALL_COLOR_R,BALL_COLOR_G,BALL_COLOR_B
;	EntityShininess BALL,BALL_SPECULAR
	
;	PositionEntity BALL,0,BAT_THICKSIZE+(MeshHeight(BALL)*0.5),GENERIC_Z_OFFSET,True
	
;	EntityRadius BALL,BALL_SIZE,BALL_SIZE
;	EntityType BALL,COLLISION_BALL
	
;	BALLVELOCITY_X=Sin(BALLINITIALANGLE)*BALL_INITIALSPEED
;	BALLVELOCITY_Y=Cos(BALLINITIALANGLE)*BALL_INITIALSPEED
	
;	BallShadow
End Function

Function InitialiseBat()
	BAT=CreatePivot()
	
	BAT_LENGTHSIZE#=BAT_DEF_LENGTHSIZE
	
	EntityType BAT,COLLISION_BAT
	EntityBox BAT,0-(BAT_LENGTHSIZE#),0,0,BAT_LENGTHSIZE#*2.0,BAT_THICKSIZE,1
	
	BATMID=CreateCylinder(POLYCOUNT_DETAIL,False,BAT)
	RotateMesh BATMID,0,0,CYLINDER_ALIGNMENT_ANGLE
	ScaleMesh BATMID,BAT_LENGTHSIZE#,BAT_THICKSIZE,BAT_THICKSIZE
	EntityColor BATMID,BAT_COLOR_R,BAT_COLOR_G,BAT_COLOR_B
	EntityAlpha BATMID,BAT_MID_TRANSPARENCY_RATIO
	;EntityBlend BATMID,BAT_TRANSPARENCYBLENDING
	;EntityFX BATMID,BATMID_FX
	
;	EntityType BATMID,COLLISION_BAT
;	EntityRadius BATMID,BAT_LENGTHSIZE,BAT_THICKSIZE
	
	BATENDL=CreateSphere(POLYCOUNT_DETAIL,BAT)
	ScaleMesh BATENDL,BAT_THICKSIZE,BAT_THICKSIZE,BAT_THICKSIZE
	MoveEntity BATENDL,0-BAT_LENGTHSIZE#,0,0
	EntityColor BATENDL,BAT_COLOR_R,BAT_COLOR_G,BAT_COLOR_B
	EntityShininess BATENDL,BAT_SPECULAR_RATIO
	
;	EntityType BATENDL,COLLISION_BAT
;	EntityRadius BATENDL,BAT_THICKSIZE,BAT_THICKSIZE
	
	BATENDR=CreateSphere(POLYCOUNT_DETAIL,BAT)
	ScaleMesh BATENDR,BAT_THICKSIZE,BAT_THICKSIZE,BAT_THICKSIZE
	MoveEntity BATENDR,BAT_LENGTHSIZE#,0,0
	EntityColor BATENDR,BAT_COLOR_R,BAT_COLOR_G,BAT_COLOR_B
	EntityShininess BATENDR,BAT_SPECULAR_RATIO
	
;	EntityType BATENDR,COLLISION_BAT
;	EntityRadius BATENDR,BAT_THICKSIZE,BAT_THICKSIZE
	
	BATINT=CreateCylinder(POLYCOUNT_DETAIL,False,BATMID)
	RotateMesh BATINT,0,0,CYLINDER_ALIGNMENT_ANGLE
	ScaleMesh BATINT,BAT_LENGTHSIZE#,BAT_THICKSIZE*BAT_INNERTHICKSIZE,BAT_THICKSIZE*BAT_INNERTHICKSIZE
	EntityFX BATINT,BATINT_FX
	EntityBlend BATINT,BAT_TRANSPARENCYBLENDING
	
	Local BatTexture=InitialiseBatTexture()
	EntityTexture BATINT,BatTexture
	FreeTexture BatTexture
	
	BatShadow
End Function

Function InitialiseBatTexture()
	Local BatTexture=CreateTexture(BATINTERNALETEXTURE_WIDTH,BATINTERNALETEXTURE_HEIGHT,BATINTERNALETEXTUREFLAGS)
	Local Buffer=TextureBuffer(BatTexture)
	Local Y
	Local X
	
	LockBuffer Buffer
	
	For Y=0 To BATINTERNALETEXTURE_HEIGHT-1
		For X=0 To BATINTERNALETEXTURE_WIDTH-1 Step 2
			WritePixelFast X,Y,-65781,Buffer
		Next
	Next
	
	UnlockBuffer Buffer
	Return BatTexture		
End Function

Function InitialiseBricks()
	
	BRICKW=BOUNDARY_W/PATTERN_BRICKS_W
	BRICKH=((BOUNDARY_H*0.15)/PATTERN_BRICKS_H)*2
	
	BRICK_MASTER_REGULAR=CreateCube()
	ScaleMesh BRICK_MASTER_REGULAR,BRICKW,BRICKH,BRICKD
	
;	BRICK_MASTER_TOUGHENED=CreateCube()
;	ScaleMesh BRICK_MASTER_TOUGHENED,BRICKW,BRICKH,BRICKD
	
	BRICK_MASTER_INVINCIBLE=CreateCube()
	ScaleMesh BRICK_MASTER_INVINCIBLE,BRICKW,BRICKH,BRICKD
	
	InitialiseBrickTextures
	
	EntityTexture BRICK_MASTER_REGULAR,BRICKTEXTURE_REGULAR
;	EntityTexture BRICK_MASTER_TOUGHENED,BRICKTEXTURE_TOUGHENED
	EntityTexture BRICK_MASTER_INVINCIBLE,BRICKTEXTURE_REGULAR;,BRICKTEXTURE_INVINCIBLE
	
	FreeTexture BRICKTEXTURE_REGULAR
;	FreeTexture BRICKTEXTURE_TOUGHENED
;	FreeTexture BRICKTEXTURE_INVINCIBLE
	
	PositionEntity BRICK_MASTER_REGULAR,0,-100,GENERIC_Z_OFFSET
;	PositionEntity BRICK_MASTER_TOUGHENED,0,-100,GENERIC_Z_OFFSET
	PositionEntity BRICK_MASTER_INVINCIBLE,0,-100,GENERIC_Z_OFFSET
	
	HideEntity BRICK_MASTER_REGULAR
;	HideEntity BRICK_MASTER_TOUGHENED
	HideEntity BRICK_MASTER_INVINCIBLE
	
End Function

Function InitialiseBrickTextures()
	BRICKTEXTURE_REGULAR=CreateBrickTextureRegular()
;	BRICKTEXTURE_TOUGHENED=CreateBrickTextureToughened()
;	BRICKTEXTURE_INVINCIBLE;=CreateBrickTextureInvincible()
End Function

Function InitialisePatterns()
	RemoveAllBricks
End Function

Function InitialiseAliens()
	CreateAlienMasters
End Function

Function  InitialiseSpecialMasterMeshes()
	Local n
	For n=SPECIAL_SCORE To SPECIAL_MAX
		SPECIAL_MASTER[n]=PopulateSpecialMasterMesh(n)
	Next
End Function

Function InitialisePhysics()
	Collisions COLLISION_BAT,COLLISION_BALL,3,1
	Collisions COLLISION_BAT,COLLISION_ALIEN,3,1
	
	Collisions COLLISION_BALL,COLLISION_BAT,3,1
	Collisions COLLISION_BALL,COLLISION_BOUND,2,1
	
	;This is it, thank you James! Bat is no longer problematic at boundary edges. No collision only check for X position value.
;	Collisions COLLISION_BAT,COLLISION_BOUND,3,1
	
	Collisions COLLISION_BALL,COLLISION_BRICK,2,1;3,1
	Collisions COLLISION_BALL,COLLISION_ALIEN,1,1
	
	Collisions COLLISION_ALIEN,COLLISION_BALL,1,1
	Collisions COLLISION_ALIEN,COLLISION_BOUND	,3,1
	Collisions COLLISION_ALIEN,COLLISION_BAT,2,1
End Function

Function InitialiseBoundaries()
	BOUND_LEFT=CreateCube()
	ScaleMesh BOUND_LEFT,1,BOUNDARY_H,1
	PositionEntity BOUND_LEFT,0-BOUNDARY_W,0,GENERIC_Z_OFFSET,True
	EntityType BOUND_LEFT,COLLISION_BOUND
;	EntityBox BOUND_LEFT,0,0,0,1,BOUNDARY_H,1
	
	EntityAlpha BOUND_LEFT,BOUNDARY_TRANSPARENCY#
	
	BOUND_RIGHT=CreateCube()
	ScaleMesh BOUND_RIGHT,1,BOUNDARY_H,1
	PositionEntity BOUND_RIGHT,BOUNDARY_W,0,GENERIC_Z_OFFSET,True
	EntityType BOUND_RIGHT,COLLISION_BOUND
;	EntityBox BOUND_RIGHT,0,0,0,1,BOUNDARY_H,1
	
	EntityAlpha BOUND_RIGHT,BOUNDARY_TRANSPARENCY#
	
	BOUND_UP=CreateCube()
	ScaleMesh BOUND_UP,BOUNDARY_W,1,1
	PositionEntity BOUND_UP,0,BOUNDARY_H,GENERIC_Z_OFFSET,True
	EntityType BOUND_UP,COLLISION_BOUND
;	EntityBox BOUND_UP,0,0,0,BOUNDARY_W,1,1
	
	EntityAlpha BOUND_UP,BOUNDARY_TRANSPARENCY#
	
	BOUNDARY_BRICK_START_Y=(BOUNDARY_H*0.65)-(BRICKH*(PATTERN_BRICKS_H+1))
	
End Function

Function InitialiseRound()
	WriteLog("Initialising Round #"+Str(ROUND))
	
	CURRENT_BACKDROP_FRAME= ROUND Mod BACKDROP_TEXTURECOUNTFRAME

	ChangeBackDropTexture
	;BAT_SHINE_COLOUR=(GetLightColour((CURRENT_BACKDROP_FRAME Mod COLOURS_SELECTION)+1)-1)
	
	SetRoundPattern
	InitialiseRoundFeatures
	
	IMPOSSIBLE_BRICKS_REMOVED=False
	INCREASING_FRAME_TIMER=0
	
	If (STATE=STATE_GAME)
		AddRoundFeatures
		ChangeMusic(Str(ROUND Mod THEMECOUNT)+".ogg")
		AudioPoints
	Else
		If (STATE=STATE_ATTRACT) Then ATTRACT_DEMO_TIMESTAMP=MilliSecs()
	End If
	
	ALIEN_SPAWN_TIMESTAMP=MilliSecs()+ATTRACT_DEMO_DURATION+ALIEN_DELAY
	ROUND_MAX_ALIENS=ROUND
	
	If (ROUND_MAX_ALIENS>TOTAL_MAX_ALIENS)
		ROUND_MAX_ALIENS=TOTAL_MAX_ALIENS
	End If
	WriteLog("Total Aliens concurrently possible this round: "+Str(ROUND_MAX_ALIENS))
	WriteLog("First alien spawn due in : "+Str((ALIEN_SPAWN_TIMESTAMP-MilliSecs())*0.001)+" seconds")
	Reset
	
	WriteLog("Round #"+Str(ROUND)+" initialised")
	
End Function

Function InitialiseState()
	ChangeToAttractMode(False)
End Function

Function InitialiseLogFile()
	Local File=WriteFile(LOGPATH)
	If (Not(File))
		DebugLog("No logging")
		DEBUG=False
	Else
		CloseFile File
	End If
End Function

Function InitialiseScoreBoard()
	CreateNumbersHUDIconImage
	CreateLettersHUDIconImage
	CreateScoreBoard
	CreateHiScoreBoard
End Function

Function InitialiseHUD()
	CreateLivesLeftHUDIcons()
	InitialiseScoreBoard
End Function

Function InitialiseRoundFeatures()
	WriteLog("Initialising Round Features")
;	HideEntity MIRROR
	RotateEntity BACKDROP,CYLINDER_ALIGNMENT_ANGLE,0,0,True
;	RotateEntity MIRROR,0-CYLINDER_ALIGNMENT_ANGLE,0,0,True
	PositionEntity BACKDROP,0,0,GENERIC_Z_OFFSET+BACKDROP_DISTANCE_OFFSET,True
;	PositionEntity MIRROR,0,0,GENERIC_Z_OFFSET+BACKDROP_DISTANCE_OFFSET+1,True
	;OBJECT_SHADOWS_ON=True
	
	EntityAlpha BACKDROP,1
	AmbientLight AMBIENT,AMBIENT,AMBIENT
	RotateEntity CAMERA,0,0,0,True
	
End Function
;~IDEal Editor Parameters:
;~F#0#21#30#34#61#65#6A#70#96#A5#B9#CC#DD#10F#121#143#149#14D#151#158
;~F#16A#187#1AB#1AF#1B9#1C0#1C5
;~C#Blitz3D