Const TOTAL_MAX_ALIENS=12

Const ALIEN_BOUNDARY_INSET=3

Const ALIEN_TYPE_MAX=5

Const ALIEN_DELAY=15000

Const ALIEN_SIZE#=1.0
Const ALIEN_INNERSIZE#=0.9
Const ALIEN_MINOR_SIZE#=0.45
Const ALIEN_THIN_AXIS#=0.001
Const ALIEN_SPECULAR_RATIO#=0.85
Const ALIEN_FX=17
Const ALIEN_TRANSPARENCY_RATIO#=0.667
Const ALIEN_TRANSPARENCYBLENDING=3

Const ALIEN_TEXTUREFLAGS=783

Const ALIEN_TEXTUREWIDTH=32
Const ALIEN_TEXTUREHEIGHT=32

Const ALIEN_ROTATION_SPEED#=10.0
Const ALIEN_SPEED#=0.2
Const ALIEN_MOTION_DURATION=750

Global ALIEN_MASTER[ALIEN_TYPE_MAX]

Global ROUND_MAX_ALIENS
Global ALIEN_COUNT
Global ALIEN_SPAWN_TIMESTAMP

Type ALIEN
	Field Entity
	Field R,G,B
	Field Rt,Gt,Bt
	Field Shadow.Shadow
	Field LastDirChange
	Field TargetAngle#
	Field Direction#
End Type

Function GetAlienFromEntity.ALIEN(Entity)
	Return Object.Alien(Int(EntityName(Entity)))
End Function

Function GetRandomAlienSide()
	Return Rand(1,3)
End Function

Function GetRandomAlienX#(Side)
	Select Side
		Case 1:
			;Left
			Return 0-(BOUNDARY_W-ALIEN_BOUNDARY_INSET)
		Case 2:
			;Right
			Return BOUNDARY_W-ALIEN_BOUNDARY_INSET
		Case 3:
			;Top
			Return Rnd(0-(BOUNDARY_W-ALIEN_BOUNDARY_INSET),BOUNDARY_W-ALIEN_BOUNDARY_INSET)
	End Select
End Function

Function GetRandomAlienY#(Side)
	Select Side
		Case 1:
			;Left
			Return Rand(BOUND_DEATHZONE_Y+ALIEN_BOUNDARY_INSET,BOUNDARY_H-ALIEN_BOUNDARY_INSET)
		Case 2:
			;Right
			Return Rand(BOUND_DEATHZONE_Y+ALIEN_BOUNDARY_INSET,BOUNDARY_H-ALIEN_BOUNDARY_INSET)
		Case 3:
			;Top
			Return BOUNDARY_H-ALIEN_BOUNDARY_INSET
	End Select
End Function

Function KillAlien(A.ALIEN)
	
	PlayAlienSound(SND_ALIEN_DESTROY)
	
	
	CreateExplosion(EntityX#(A\Entity ,True),EntityY#(A\Entity,True),A\R,A\G,A\B)
	DestroyAlien(A)
End Function

Function DestroyAllAliens()
	Local A.ALIEN
	Local Counter
	For A=Each ALIEN
		If (A<>Null)
			DestroyAlien(A,False)
			Counter=Counter+1
		End If
	Next
	
	If Counter Then WriteLog("Destroyed all "+Str(Counter)+" aliens")
End Function

Function DestroyAlien(A.ALIEN,NotifyLog=True)
	ALIEN_COUNT=ALIEN_COUNT-1
	If (NotifyLog) Then WriteLog("Alien destroyed. Current count: "+Str(ALIEN_COUNT))
	FreeShadow(A\Shadow)
	FreeEntity A\Entity
	Delete A
End Function

Function SpawnAlien(X#,Y#)
	Local A.ALIEN=New ALIEN
	
	Local T=Rand(0,ALIEN_TYPE_MAX)
	A\Entity=CopyEntity(ALIEN_MASTER[T])
	
	A\R=RandomColourComponent()
	A\G=RandomColourComponent()
	A\B=RandomColourComponent()
	
	A\Rt=RandomColourComponent()
	A\Gt=RandomColourComponent()
	A\Bt=RandomColourComponent()
	
	A\Direction=Rnd(0,360)
	A\LastDirChange=FRAMETIMESTAMP
	A\Shadow=CreateShadow(1,1,A\Entity)
	A\TargetAngle=Rnd(0,360)
	
	RotateEntity A\Entity,0,A\Direction,-0,True
	
	PositionEntity A\Entity,X,Y,GENERIC_Z_OFFSET,True
	
	EntityType A\Entity,COLLISION_ALIEN
	EntityRadius A\Entity,1,1
	
	NameEntity A\Entity,Str(Handle(A))
	
	ALIEN_COUNT=ALIEN_COUNT+1
	
	
	PlayAlienSound(SND_ALIEN_SPAWN)
	
	
	WriteLog("Spawned Alien #"+Str(ALIEN_COUNT)+" Type "+Str(T)+" at "+Str(X)+","+Str(Y))
	
End Function

Function CreateAlienMasters()
	Local Iterate
	For Iterate=0 To ALIEN_TYPE_MAX
		ALIEN_MASTER[Iterate]=CreateAlienTemplate(Iterate)
		HideEntity ALIEN_MASTER[Iterate]
	Next
End Function

Function CreateAlienTemplate(n)
	Local Mesh
	Local Aid
	
	Select n
		Case 0:
			Mesh=CreateCone(POLYCOUNT_DETAIL,True)
			Aid=CreateTorus(POLYCOUNT_DETAIL,ALIEN_INNERSIZE,ALIEN_MINOR_SIZE)
			RotateMesh Aid,90,0,0
			PositionMesh Mesh,0.0,ALIEN_INNERSIZE,0.0
		Case 1:
			Mesh=CreateCylinder(POLYCOUNT_DETAIL,True)
			ScaleMesh Mesh,1,ALIEN_THIN_AXIS,1
			RotateMesh Mesh,0,0,0
		Case 2:
			Mesh=CreateSphere(POLYCOUNT_DETAIL)
			Aid=CreateSphere(POLYCOUNT_DETAIL)
			ScaleMesh Aid,ALIEN_MINOR_SIZE,ALIEN_MINOR_SIZE,ALIEN_MINOR_SIZE
			PositionMesh Aid,1+ALIEN_MINOR_SIZE,0,0
			AddMesh Aid,Mesh
			FreeEntity Aid 
			
			Aid=CreateSphere(POLYCOUNT_DETAIL)
			ScaleMesh Aid,ALIEN_MINOR_SIZE,ALIEN_MINOR_SIZE,ALIEN_MINOR_SIZE
			PositionMesh Aid,0-(1+ALIEN_MINOR_SIZE),0,0
			
		Case 3:
			Mesh=CreateCone(3,True)
			ScaleMesh Mesh,Sqr(2),1,Sqr(2)
			
		Case 4:
			Mesh=CreateCube()
			
		Case 5:
			Mesh=CreateHemisphere(POLYCOUNT_DETAIL)
			Aid=CreateCylinder(POLYCOUNT_DETAIL,True)
			ScaleMesh Aid,1,ALIEN_THIN_AXIS,1
			RotateMesh Aid,0,0,90
	End Select
	
	If (Aid)
		AddMesh Aid,Mesh
		FreeEntity Aid 
	End If
	
	Return Mesh
End Function

Function UpdateAliens()
	Local ms=FRAMETIMESTAMP
	
	If ((ms-ALIEN_SPAWN_TIMESTAMP)>ALIEN_DELAY)
		ALIEN_SPAWN_TIMESTAMP=ms+(Rand(ALIEN_DELAY*0.5,ALIEN_DELAY*2.0))
		If (ALIEN_COUNT<ROUND_MAX_ALIENS)
			Local Side=GetRandomAlienSide()
			Local X#=GetRandomAlienX(Side)
			Local Y#=GetRandomAlienY(Side)
			SpawnAlien(X#,Y#)
			WriteLog("Next alien spawn due in "+Str((ALIEN_SPAWN_TIMESTAMP-ms)*0.001)+" seconds")
		End If
	End If
	
	Local A.ALIEN
	For A=Each ALIEN
		UpdateAlien(A)
	Next
End Function

Function UpdateAlien(A.ALIEN)
	UpdateAlienColour(A)
	MoveAlien(A)
	AnimateAlien(A)
End Function

Function AnimateAlien(A.ALIEN)
	RotateEntity A\Entity,A\R,A\G,A\B,True
End Function

Function MoveAlien(A.ALIEN)
	Local X#=RoundedTrig#(Sin#(A\Direction#))
	Local Y#=RoundedTrig#(Cos#(A\Direction#))
	TranslateEntity A\Entity,X#*ALIEN_SPEED#,Y#*ALIEN_SPEED#,0,True
	Local Difference#=(RoundedTrig#(Float(A\TargetAngle#-A\Direction#)))
	Local Change=Sgn(Difference#)
	
	A\Direction=A\Direction+(Float(Change*ALIEN_ROTATION_SPEED))
	
	If (Float(Abs(Float(A\Direction#-A\TargetAngle#)))<(ALIEN_ROTATION_SPEED#*1.5))
		A\Direction#=A\TargetAngle#
		
		If ((FRAMETIMESTAMP-A\LastDirChange)>ALIEN_MOTION_DURATION)
			A\TargetAngle#=Rnd(0.0,360.0)
			A\LastDirChange=FRAMETIMESTAMP
		End If
	End If
	
	AlienBoundary(A)
End Function
	
Function UpdateAlienColour(A.ALIEN)
	Local Rd=Sgn(A\Rt-A\R)
	Local Gd=Sgn(A\Gt-A\G)
	Local Bd=Sgn(A\Bt-A\B)
	
	A\R=A\R+Rd
	A\G=A\G+Gd
	A\B=A\B+Bd
	
	If ( (A\R=A\Rt) And (A\G=A\Gt) And (A\B=A\Bt) )
		A\Rt=RandomColourComponent()
		A\Gt=RandomColourComponent()
		A\Bt=RandomColourComponent()
	End If
	
	EntityColor A\Entity,A\R,A\G,A\B
End Function
;~IDEal Editor Parameters:
;~F#20#2A#2E#32#40#4E#57#64#6C#92#9A#CA#DE#E4#E8#FD
;~C#Blitz3D