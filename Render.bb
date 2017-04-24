Include"Shadow.bb"
Include"Shine.bb"

Const MAX_FRAME_INTERVAL#=16.6667

Const GWIDTH=1024
Const GHEIGHT=768
Const GDEPTH=32

Const DEF_TEXTURE_FLAGS=769

Const CAMERA_HEIGHT=20
Const CAMERA_XOFFSET=0
Const CAMERA_DISTANCE=50

Const AMBIENT=128

Const SUN_PITCH=30
Const SUN_YAW=-45
Const SUN_X=-20
Const SUN_Y=20
Const SUN_Z=-10

Const GENERIC_Z_OFFSET#=0.0

Const POLYCOUNT_DETAIL=8

Const CYLINDER_ALIGNMENT_ANGLE#=-90.0

Const GREYTONE=-8355712

;Bits 1=RED 2=GREEN 4=Blue
Const COL_SELECTION_RED=1
Const COL_SELECTION_GREEN=2
Const COL_SELECTION_YELLOW=3
Const COL_SELECTION_BLUE=4
Const COL_SELECTION_MAGENTA=5
Const COL_SELECTION_CYAN=6
Const COL_SELECTION_GREY=7

Const COLOURS_SELECTION=6;GREY Not included

Global SUN
Global CAMERA

Global FRAMETIMESTAMP
Global INCREASING_FRAME_TIMER

Function CallRender()
	UpdateWorld
	RenderWorld
	
	ShowHud
	Flip False
	
	FrameLimit
End Function

Function FrameLimit()
	Local Frametime=(MilliSecs()-FRAMETIMESTAMP)
	If (Frametime<MAX_FRAME_INTERVAL)
		Local DTime=MAX_FRAME_INTERVAL-Frametime
		Delay DTime
	End If
	INCREASING_FRAME_TIMER=INCREASING_FRAME_TIMER+1
End Function

Function GetLightColour(ColourChoice)
	Local R
	Local G
	Local B
	
	Select ColourChoice
		Case COL_SELECTION_RED:
			R=255
			G=160
			B=160
			
		Case COL_SELECTION_GREEN:
			R=160
			G=255
			B=160	
		Case COL_SELECTION_BLUE:
			R=160
			G=160
			B=255
		Case COL_SELECTION_YELLOW:
			R=255
			G=255
			B=64
		Case COL_SELECTION_CYAN:
			R=64
			G=255
			B=255
		Case COL_SELECTION_MAGENTA:
			R=255
			G=64
			B=255	
			
		Case COL_SELECTION_GREY:
			R=192
			G=192
			B=192	
			
	End Select
	
	Return R + (G Shl 8)+(B Shl 16)
End Function	

Function GetR(ColourSelection)
	Select ColourSelection
		Case COL_SELECTION_RED,COL_SELECTION_YELLOW,COL_SELECTION_MAGENTA,COL_SELECTION_GREY:
			Return 255
		Default: Return 0
	End Select
End Function

Function GetG(ColourSelection)
	Select ColourSelection
		Case COL_SELECTION_GREEN,COL_SELECTION_CYAN,COL_SELECTION_YELLOW,COL_SELECTION_GREY:
			Return 255
		Default: Return 0
	End Select
End Function

Function GetB(ColourSelection)
	Select ColourSelection
		Case COL_SELECTION_BLUE,COL_SELECTION_CYAN,COL_SELECTION_MAGENTA,COL_SELECTION_GREY:
			Return 255
		Default: Return 0
	End Select
End Function

Function CopyCharImageToTexture(him_Image,TextureFlags=DEF_TEXTURE_FLAGS)
	Local htx_Texture=CreateTexture(HUD_SCORE_CHARACTERWIDTH,HUD_SCORE_CHARACTERHEIGHT,TextureFlags)
	CopyRect 0,0,HUD_SCORE_CHARACTERWIDTH,HUD_SCORE_CHARACTERHEIGHT,0,0,ImageBuffer(him_Image),TextureBuffer(htx_Texture)
	
;	Local Y
;	Local X
;	Local Buffer=TextureBuffer(htx_Texture)
;	LockBuffer Buffer
	
;	For Y=0 To HUD_SCORE_CHARACTERHEIGHT-1
;		For X=0 To HUD_SCORE_CHARACTERWIDTH-1
;			If (ReadPixelFast(X,Y,Buffer)<1)
;				WritePixelFast X,Y,0,Buffer
;			End If
;		Next
;	Next
;	UnlockBuffer Buffer 
	
	Return htx_Texture 
End Function

Function GetDarkColour(ColourChoice)
	Local R
	Local G
	Local B
	
	Select ColourChoice
		Case COL_SELECTION_RED:
			R=160
			G=16
			B=16
			
		Case COL_SELECTION_GREEN:
			R=16
			G=160
			B=16
		Case COL_SELECTION_BLUE:
			R=16
			G=16
			B=160
		Case COL_SELECTION_YELLOW:
			R=80
			G=80
			B=32
		Case COL_SELECTION_CYAN:
			R=32
			G=80
			B=80
		Case COL_SELECTION_MAGENTA:
			R=80
			G=32
			B=80
		Case COL_SELECTION_GREY:
			R=64
			G=64
			B=64
			
	End Select
	
	Return R + (G Shl 8)+(B Shl 16)
End Function	

Function CreateQuad(Parent=False)
	Local Mesh=CreateMesh(Parent)
	Local Surface=CreateSurface(Mesh)
	
	AddVertex(Surface,-0.5,0.5,0,0,0)
	VertexColor Surface,0,255,255,255
	AddVertex(Surface,0.5,0.5,0,1,0)
	VertexColor Surface,1,255,255,255
	AddVertex(Surface,-0.5,-0.5,0,0,1)
	VertexColor Surface,2,255,255,255
	AddVertex(Surface,0.5,-0.5,0,1,1)
	VertexColor Surface,3,255,255,255
	AddTriangle(Surface,0,1,2)
	AddTriangle(Surface,3,2,1)
	
	UpdateNormals Mesh
	
	Return Mesh
End Function

Function CreateTorus(Segments=8,Radius#=0.5, Width#=0.5, Parent=0)
	
	Local Mesh=CreateMesh(Parent)
	Local Surface=CreateSurface(Mesh)
	
	Local RadialAngle#=0
	Local CircumAngle#=0
	Local AngleStep#=Float(360.0/Float(Segments))
	
	Local RadLength#
	
	Local X#
	Local Y#
	Local Z#
	
	Local U#
	Local V#
	Local W#
	
	Local V0
	Local V1
	Local V2
	Local V3
	
	While (RadialAngle<360.0)
		
		RadLength#= (Radius + (Width*Sin(RadialAngle)))
		
		Z=Cos(RadialAngle)*Width
		CircumAngle=0.0
		
		While (CircumAngle<360.0)
			X=(Cos(CircumAngle))*RadLength
			Y=(Sin(CircumAngle))*RadLength
			
			U=RadialAngle/360.0
			V=CircumAngle/360.0
			
			AddVertex(Surface,X,Y,Z,U,V,W)
			CircumAngle=CircumAngle+AngleStep
		Wend
		
		RadialAngle=RadialAngle+AngleStep
	Wend
	
	Local Iterverts
	
	For Iterverts=0 To (Segments*Segments)-1
		V0=Iterverts
		V1=Iterverts+Segments
		V2=V0+1
		V3=V1+1
		
		V1=V1*(V1<(Segments*Segments))
		V2=V2*(V2<(Segments*Segments))
		V3=V3*(V3<(Segments*Segments))
		
		AddTriangle Surface,V0,V1,V2
		AddTriangle Surface,V1,V3,V2	
	Next	
	UpdateNormals Mesh
	Return Mesh
	
End Function 

Function CreateHemisphere(Segments=32,Parent=False,nb_Pole=0)
	Local Temp
	Local Mesh
	Local OldSurface
	Local OldVertex
	Local OldTriangle
	Local NewSurface
	Local OldVertex0
	Local OldVertex1
	Local OldVertex2
	Local NewVertex0
	Local NewVertex1
	Local NewVertex2
	
	Temp = CreateSphere(Segments)
	
	RotateMesh Temp,0,0,90
	
	OldSurface = GetSurface( Temp, 1 )
	Local hv_IterVertex
	
	If (nb_Pole)
		For hv_IterVertex = 0 To CountVertices(OldSurface)-1
			If VertexY( OldSurface, hv_IterVertex ) <= 0
				VertexCoords OldSurface, hv_IterVertex ,VertexX(OldSurface,hv_IterVertex), 0 , VertexZ(OldSurface,hv_IterVertex)
				VertexNormal OldSurface, hv_IterVertex, 0, 0, 0
			EndIf
		Next
	Else
		For hv_IterVertex= 0 To CountVertices(OldSurface)-1
			If VertexY( OldSurface, hv_IterVertex ) > 0
				VertexCoords OldSurface, hv_IterVertex, VertexX(OldSurface,hv_IterVertex), 0 , VertexZ(OldSurface,hv_IterVertex)
				VertexNormal OldSurface, hv_IterVertex, 0, 0, 0
			EndIf
		Next
	End If
	
	RotateMesh Temp,0,0,-90
	
	Mesh = CreateMesh(Parent)
	
	NewSurface = CreateSurface(Mesh)
	For OldTriangle = 0 To CountTriangles(OldSurface )-1
		OldVertex0 = TriangleVertex( OldSurface, OldTriangle, 0 )
		OldVertex1 = TriangleVertex( OldSurface, OldTriangle, 1 )
		OldVertex2 = TriangleVertex( OldSurface, OldTriangle, 2 )
		If ((VertexNY( OldSurface,OldVertex0 ) <> 0) Or (VertexNY( OldSurface, OldVertex1 ) <> 0) Or (VertexNY( OldSurface, OldVertex2) <> 0))
			NewVertex0 = AddVertex( NewSurface, VertexX( OldSurface, OldVertex0 ), VertexY( OldSurface, OldVertex0 ) , VertexZ( OldSurface, OldVertex0 ),VertexU( OldSurface, OldVertex0 ),VertexV( OldSurface, OldVertex0 ),VertexW( OldSurface, OldVertex0 ))
			NewVertex1 = AddVertex( NewSurface, VertexX( OldSurface, OldVertex1 ), VertexY( OldSurface,OldVertex1 ) , VertexZ( OldSurface,OldVertex1 ),VertexU( OldSurface, OldVertex1 ),VertexV( OldSurface, OldVertex1 ),VertexW( OldSurface, OldVertex1 ))
			NewVertex2 = AddVertex( NewSurface, VertexX( OldSurface, OldVertex2 ), VertexY( OldSurface, OldVertex2 ) , VertexZ( OldSurface, OldVertex2 ),VertexU( OldSurface, OldVertex2 ),VertexV( OldSurface, OldVertex2 ),VertexW( OldSurface, OldVertex2 ))
			
			VertexNormal NewSurface, NewVertex0, VertexNX( OldSurface, OldVertex0 ) , VertexNY(OldSurface, OldVertex0 ) , VertexNZ( OldSurface, OldVertex0 )
			VertexNormal NewSurface, NewVertex1, VertexNX( OldSurface, OldVertex1 ) , VertexNY(OldSurface, OldVertex1 ) , VertexNZ( OldSurface, OldVertex1 )
			VertexNormal NewSurface, NewVertex2, VertexNX( OldSurface, OldVertex2 ) , VertexNY(OldSurface, OldVertex2 ) , VertexNZ( OldSurface, OldVertex2 )
			
			VertexColor NewSurface,NewVertex0,255,255,255
			VertexColor NewSurface,NewVertex1,255,255,255
			VertexColor NewSurface,NewVertex2,255,255,255
			
			AddTriangle NewSurface, NewVertex0, NewVertex1, NewVertex2
		EndIf
	Next
	
	FreeEntity Temp
	
	UpdateNormals Mesh
	
	If (Parent) Then EntityParent Mesh,Parent,True
	
	Return Mesh
End Function

Function RandomColourComponent()
	Local C=Rand(64,192)
	If (Rand(0,1))
		C=255-C
	End If
	Return C
End Function
;~IDEal Editor Parameters:
;~F#30#3A#43#6D#75#7D#85#9A#C3#D7#118#160
;~C#Blitz3D