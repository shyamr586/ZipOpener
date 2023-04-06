#ifndef FILEPICKER_H
#define FILEPICKER_H

#include <QObject>
#include <QProperty>

class FilePicker : public QObject
{
    Q_OBJECT
    Q_PROPERTY(QStringList files READ files WRITE setFiles NOTIFY filesChanged)

public:
    explicit FilePicker(QObject *parent = nullptr);
    QStringList files() const;
    void setFiles(const QStringList &newFiles);
    Q_INVOKABLE void openFileDialog();

signals:
    void filesChanged();

private:
    QStringList m_files;
};

#endif // FILEPICKER_H
