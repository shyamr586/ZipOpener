import QtQuick
import QtQuick.Window
import Filepicker

Window {
    width: 640
    height: 480
    visible: true
    title: qsTr("Hello World")

    Filepicker {id: filepicker}

    Rectangle {
        id: button
        anchors {top: parent.top; topMargin: 30; horizontalCenter: parent.horizontalCenter}
        height: 50; width: 120
        radius: 50
        color: "yellow"
        Text { text: "Open dialog"; anchors.centerIn: parent }
        MouseArea {anchors.fill: parent; onClicked: {filepicker.openFileDialog(); heading.text = "The files present inside the zip file is: "}}
    }

    Text {id: loader; width: parent.width; anchors.top: heading.bottom; anchors.topMargin: 40; font.pixelSize: 20;text: "Unzipping progress: "+filepicker.loaded+" bytes read."}
    Text {id: heading; width: parent.width; anchors.top: button.bottom; anchors.topMargin: 40; font.pixelSize: 20}

    Item {
        id: listviewcontainer
        anchors.top: loader.bottom
        anchors.topMargin: 10
        width: parent.width; height: parent.height - 70

        ListView {
            id: listview
            model: filepicker.files
            spacing: 10
            anchors.fill: parent
            clip: true


            delegate: Rectangle {
                color: "light blue"
                width: parent.width; height: 80
                Text {anchors.centerIn: parent; text: filepicker.files[index]}
            }
        }
    }

}
