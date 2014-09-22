This application demonstrates how to use SqlLiteOpenHelper.

1) Step 1: create new project and copy all resources and codes from the project "Ch4ToDoAdapter"
2) Step 2: create ToDoContentProvider class
3) Step 3: register content uri in ApplicationManifest.xml like
	<provider
            android:name=".ToDoContentProvider"
            android:authorities="com.android.tuto.todoprovider" />
4) Step 3: update the ToDoListActivity to finish the example