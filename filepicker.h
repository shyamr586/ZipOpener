#ifndef FILEPICKER_H
#define FILEPICKER_H

#include <QObject>
#include <QProperty>

class FilePicker : public QObject
{
    Q_OBJECT
    Q_PROPERTY(QString filepath READ filepath WRITE setFilepath NOTIFY filepathChanged)

public:
    explicit FilePicker(QObject *parent = nullptr);

    QString filepath() const;
    void setFilepath(const QString &newFilepath);
    Q_INVOKABLE void openFile();

signals:
    void filepathChanged();

private:
    QString m_filepath;
};

#endif // FILEPICKER_H
