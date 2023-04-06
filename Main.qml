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
        anchors.top: parent.top
        anchors.topMargin: 30
        height: 50; width: 100
        radius: 50
        color: "yellow"
        Text { text: "Open dialog"; anchors.centerIn: parent }
        MouseArea {anchors.fill: parent; onClicked: {filepicker.openFile();}}
    }

}
