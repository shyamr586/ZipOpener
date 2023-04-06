#include <QGuiApplication>
#include <QQmlApplicationEngine>
#include <QJniObject>
#include <filepicker.h>

int main(int argc, char *argv[])
{
    QGuiApplication app(argc, argv);

    qmlRegisterType<FilePicker>("Filepicker",1,0,"Filepicker");

    QQmlApplicationEngine engine;
    const QUrl url(u"qrc:/ZipFileOpener/Main.qml"_qs);
    QObject::connect(&engine, &QQmlApplicationEngine::objectCreationFailed,
        &app, []() { QCoreApplication::exit(-1); },
        Qt::QueuedConnection);
    engine.load(url);

    return app.exec();
}
