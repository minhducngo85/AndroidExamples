Preface (Vorwort):
Widget va Gadget : la nhung tien ich nho tien ich voi nguoi dung.
Su khac nhau:

Doi voi Widget, ban co the sao chep chung den bat cu noi nao ma ma HTML duoc su dung.
Dieu do co nghia la ban hoan toan co the dua nhung Widget len blog hoac trang ca nhan tren cac mang xa hoi cua minh.
Nguoc lai, Gadget chi chay tren nhung trang web cu the hoac moi truong ma chung duoc thiet ke.
--------------------------------

The resources in Android app are stored in the "res" folder, which
includes layout, values, menu and a series of drawable subfolders.
 
Types of Android application:
	1. Forceground: the application is effectively suspended when it is not visible. Games are most common examples.
	2. Background:These applications are less common, 
	but good examples include call screening application, SMS auto-responder, and alarm clocks.
	3. Intermittent: Most well-designed application fall in this category.
	Union of visible activities and invisible background services.
	4. Widgets and Liver Wallpapers  
	
======================   Manifest file ================================================================
Android's application manifest:

The manifest file describes the component in the application and how they are bound togehter.
The manifest file is also used to specify the application's metadata, its hardware and platform requirements, external libraries 
and required permission.

---------------------------------------
uses-configuration in AndroidManifest.xml:
1) reqFiveWayNav: true or fasle, If it is set as "true", it can be navigated as left, right, top, button, and clicking on the current selection
2) reqKeyboardType : it is be one of following values: nokeys, qwerty, twelvekey or undefined
3) reqNavigation: nonav, dpad, trackball, wheel or undefined
4) reqTouchScreen: notouch, stylus, finger or undefined.

in addition, uses-feature specifies the version of OpenGL ES supported.
The example below shows how to enable OpenGL ES version 1.1:

<uses-feature android:glEsVersion=”0x00010001” />

---------------------------------------------------
uses-feature in AndroidManifest.xml

uses-feature node specifies which hardware features you application requires. e.g.
<uses-feature android:name=”android.hardware.nfc” />

Currently, optional hardware features include the following:
1) Audio — For applications that requires a low-latency audio pipeline. Note that at the
time of writing this book, no Android devices satisfi ed this requirement.
2) Bluetooth — Where a Bluetooth radio is required.
3) Camera — For applications that require a camera. You can also require (or set as
options) autofocus, fl ash, or a front-facing camera.
4) Location — If you require location-based services. You can also specify either net-
work or GPS support explicitly.
5) Microphone — For applications that require audio input.
6) NFC — Requires NFC (near-fi eld communications) support.
7) Sensors — Enables you to specify a requirement for any of the potentially available
hardware sensors.
8) Telephony — Specify that either telephony in general, or a specifi c telephony radio
(GSM or CDMA) is required.
‰ Touchscreen — To specify the type of touch-screen your application requires.
‰ USB — For applications that require either USB host or accessory mode support.
‰ Wi-Fi — Where Wi-Fi networking support is required.


----------------------------------------------------
supports-screens in AndroidManifest
supports-screens node specifies the screen size, which your applications are designed for and tested to.

e.g.    <supports-screens android:smallScreens=”false”
					android:normalScreens=”true”
					android:largeScreens=”true”
					android:xlargeScreens=”true”
					android:requiresSmallestWidthDp=”480”
					android:compatibleWidthLimitDp=”600”
					android:largestWidthLimitDp=”720”/>
					
-------------------------------
supports-gl-texture — Declares that the application is capable of providing texture
assets that are compressed using a particular GL texture compression format

<supports-gl-texture android:name=”GL_OES_compressed_ETC1_RGB8_texture” />

--------------------------------------------------------------
uses-permission — As part of the security model, uses-permission tags declare the user
permissions your application requires.

<uses-permission android:name=”android.permission.ACCESS_FINE_LOCATION”/>

--------------------------------------------------------------

permission — Your application components can also create permissions to restrict access
to shared application components.

<permission android:name=”com.paad.DETONATE_DEVICE”
		android:protectionLevel=”dangerous” 
		android:label=”Self Destruct”
		android:description=”@string/detonate_description”>
</permission>

protectionLevel can be one of: normal , dangerous , signature , signatureOrSystem

-------------------------------------------------------------

instrumentation : provides a test framework for your application component at the runtime.

<instrumentation android:label=”My Test”
			android:name=”.MyTestClass”
			android:targetPackage=”com.paad.apackage”>
</instrumentation>

--------------------------------------------------------------
application : is used to specify the metadat of your application

<application android:icon=”@drawable/icon”
		android:logo=”@drawable/logo”
		android:theme=”@android:style/Theme.Light”
		android:name=”.MyApplicationClass”
		android:debuggable=”true”>
		[ ... application nodes ... ]
</application>

---------------------------------------------------------------
activity — An activity tag is required for every Activity within your application.
Each Activity node supports intent-filter child tags that define the Intents that can be used to start the Activity.

<activity android:name=”.MyActivity” android:label=”@string/app_name”>
	<intent-filter>
		<action android:name=”android.intent.action.MAIN” />
		<category android:name=»android.intent.category.LAUNCHER» />
	</intent-filter>
</activity>

--------------------------------------------------------------

service — As with the activity tag, add a service tag for each Service class
used in your application. Service tags also support intent-filter child tags to
allow late runtime binding.

<service android:name=”.MyService”>
</service>

------------------------------------------------------
provider — Provider tags specify each of your application’s Content Providers.
Content Providers are used to manage database access and sharing.

<provider android:name=”.MyContentProvider”
		android:authorities=”com.paad.myapp.MyContentProvider”/>
		
-------------------------------------------------------------------
receiver — By adding a receiver tag, you can register a Broadcast Receiver without having to launch your application first.

<receiver android:name=”.MyIntentReceiver”>
	<intent-filter>
		<action android:name=”com.paad.mybroadcastaction” />
	</intent-filter>
</receiver>

----------------------------------------------------------------------
uses-library — Used to specify a shared library that this application requires.

<uses-library android:name=”com.google.android.maps”
			android:required=”false”/>

====================== Creating External Resources ================================================
Simple Values

<?xml version=”1.0” encoding=”utf-8”?>
<resources>
	<string name=”app_name”>To Do List</string>
	<plurals name=”androidPlural”>
		<item quantity=”one”>One android</item>
		<item quantity=”other”>%d androids</item>
	</plurals>
	<color name=”app_background”>#FF0000FF</color>
	<dimen name=”default_border”>5px</dimen>
	<string-array name=“string_array“>
		<item>Item 1</item>
		<item>Item 2</item>
		<item>Item 3</item>
	</string-array>
	<array name=“integer_array“>
		<item>3</item>
		<item>2</item>
		<item>1</item>
	</array>
</resources>

you can you string resources as input parameters for String.format but you have to escpase the HTML tags. e.g.

<string name=”stop_message”>&lt;b>Stop&lt;/b>. %1$s</string>

in your code you cann access like this:

String rString = getString(R.string.stop_message);
String fString = String.format(rString, “Collaborate and listen.”);
CharSequence styledString = Html.fromHtml(fString);

you can also create alternative plurat form for input string

<plurals name=”unicornCount”>
	<item quantity=”one”>One unicorn</item>
	<item quantity=”other”>%d unicorns</item>
</plurals>

in your code:

Resources resources = getResources();
String unicornStr = resources.getQuantityString(R.plurals.unicornCount, unicornCount, unicornCount);

Colors:
Use the color tag to defi ne a new color resource. Specify the color value using a # symbol followed
by the (optional) alpha channel, and then the red, green, and blue values using one or two hexadeci-
mal numbers with any of the following notations:
	1) #RGB
	2) #RRGGBB
	3) #ARGB
	4) #AARRGGBB

Dimensions:
Dimensions are most commonly referenced within style and layout resources. They’re useful for
creating layout constants, such as borders and font heights.
To specify a dimension resource, use the dimen tag, specifying the dimension value, followed by an
identifi er describing the scale of your dimension:
	1) px (screen pixels)
	2) in (physical inches)
	3) pt (physical points)
	4) mm (physical millimeters)
	5) dp (density-independent pixels)
	6) sp (scale-independent pixels)
e.g.
<dimen name=”standard_border”>5dp</dimen>
<dimen name=”large_font_size”>16sp</dimen>

----------------------------------------------------------
styles and themes

<?xml version=”1.0” encoding=”utf-8”?>
<resources>
	<style name=”base_text”>
	<item name=”android:textSize”>14sp</item>
	<item name=”android:textColor”>#111</item>
</style>
</resources>

style supports the ineritance and make it easy to create asimple variation.

<?xml version=”1.0” encoding=”utf-8”?>
<resources>
	<style name=”small_text” parent=”base_text”>
	<item name=”android:textSize”>8sp</item>
</style>
</resources>

------------------------------------------------------------------
Drawable

Drawable resources include bitmaps and NinePatches (stretchable PNG images). They also include
complex composite Drawables, such as LevelListDrawable s and StateListDrawable s, that can be
defi ned in XML.

-------------------------------------------------------------------
Layouts

Layout resources enable you to decouple the presentation layer from the bussenis logic layer.
You can use layouts to define the UI for any visual component, including Activities, Fragments,
and Widgets.

-------------------------------------------------------------------
Animations
Android supports three types of animation:
	1) Property animations
	2) View animations
	4) Frame animations

--------------------------------------
Menu

Menu enables you to design your application menu UI in XML file rather in you code.

================================ Using Resources ========================================

referencing resources within resources

attribute=”@[packagename:]resourcetype/resourceidentifier”


Using system reources

in your code:
CharSequence httpError = getString(android.R.string.httpErrorBadUrl);

in your XML file:

<EditText
	android:id=”@+id/myEditText”
	android:layout_width=”match_parent”
	android:layout_height=”wrap_content”
	android:text=”@android:string/httpErrorBadUrl”
	android:textColor=”@android:color/darker_gray”/>
	
referencing style from the current applied theme:

using ?android
<EditText
	android:id=”@+id/myEditText”
	android:layout_width=”match_parent”
	android:layout_height=”wrap_content”
	android:text=”@android:string/httpErrorBadUrl”
	android:textColor=”?android:textColor”/>
	
=============================== multiple languages ==================

Project/
	res/
		values/
			strings.xml
		values-fr/
			strings.xml
		values-fr-rCA/
			strings.xml