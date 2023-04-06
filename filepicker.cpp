#include "filepicker.h"
#include <QGuiApplication>
#include <QJniObject>

QJniObject javaClass = QNativeInterface::QAndroidApplication::context();


FilePicker::FilePicker(QObject *parent)
    : QObject{parent}
{

}

QString FilePicker::filepath() const
{
    return m_filepath;
}

void FilePicker::setFilepath(const QString &newFilepath)
{
    if (m_filepath == newFilepath)
        return;
    m_filepath = newFilepath;
    emit filepathChanged();
}

void FilePicker::openFile(){
    javaClass.callMethod<void>("openFileDialog","()V");
}
