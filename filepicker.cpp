#include "filepicker.h"
#include <QGuiApplication>
#include <QJniObject>

QJniObject javaClass = QNativeInterface::QAndroidApplication::context();


extern "C" JNIEXPORT void JNICALL
Java_com_example_zipopener_MainActivity_fileListReceived(JNIEnv* env, jclass, jobject list, jlong pointer) {
    QJniObject arrayList = list;
    jint size = arrayList.callMethod<jint>("size","()I");

    QStringList files;

    for(jint i = 0; i < size; ++i) {
           QJniObject element = arrayList.callObjectMethod("get", "(I)Ljava/lang/Object;", i);
           QString qstring = element.toString();
           files.append(qstring);
           qDebug() << "The name of file is " << qstring;
    }

    qDebug() << "The list is" << files;
    reinterpret_cast<FilePicker*>(pointer)->setFiles(files);
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

void FilePicker::setFiles(const QStringList &newFiles)
{
    if (m_files == newFiles)
        return;
    m_files = newFiles;
    emit filesChanged();
}
