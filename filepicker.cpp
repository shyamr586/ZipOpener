#include "filepicker.h"
#include <QGuiApplication>
#include <QJniObject>
#include <QThread>

QJniObject javaClass = QNativeInterface::QAndroidApplication::context();


extern "C" JNIEXPORT void JNICALL
Java_com_example_zipopener_MainActivity_fileListReceived(JNIEnv* env, jclass, jobject list, jlong pointer) {
    QJniObject arrayList = list;
    float size = (float)(int)arrayList.callMethod<jint>("size","()I");

    QStringList files;

    for(int i = 0; i < size; i++) {
           QJniObject element = arrayList.callObjectMethod("get", "(I)Ljava/lang/Object;", i);
           QString qstring = element.toString();
           files.append(qstring);
           //qDebug() << "Percentage done is " << (i + 1)/size*100;
           reinterpret_cast<FilePicker*>(pointer)->setFiles(files);
           reinterpret_cast<FilePicker*>(pointer)->setLoaded(i+1);
           qDebug() << "The name of file is " << qstring;
    }

    //i think i can put a loader here ^ too.

    qDebug() << "The list is" << files;

}

FilePicker::FilePicker(QObject *parent)
    : QObject{parent}
{

}

void FilePicker::openFileDialog(){


    javaClass.callMethod<void>("openFileDialog","(J)V",(long long)(FilePicker*)this);
}

QStringList FilePicker::files() const
{
    return m_files;
}

int FilePicker::loaded() const
{
    return m_loaded;
}

void FilePicker::setFiles(const QStringList &newFiles)
{
    if (m_files == newFiles)
        return;
    m_files = newFiles;
    emit filesChanged();
}

void FilePicker::setLoaded(const int &newLoaded)
{
    if (m_loaded == newLoaded)
        return;
    m_loaded = newLoaded;
    emit loadedChanged();
}
