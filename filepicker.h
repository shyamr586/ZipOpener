#ifndef FILEPICKER_H
#define FILEPICKER_H

#include <QObject>
#include <QProperty>

class FilePicker : public QObject
{
    Q_OBJECT
    Q_PROPERTY(QStringList files READ files WRITE setFiles NOTIFY filesChanged)
    Q_PROPERTY(int loaded READ loaded WRITE setLoaded NOTIFY loadedChanged)

public:
    explicit FilePicker(QObject *parent = nullptr);
    QStringList files() const;
    int loaded() const;
    void setFiles(const QStringList &newFiles);
    void setLoaded(const int &newLoaded);
    Q_INVOKABLE void openFileDialog();

signals:
    void filesChanged();
    void loadedChanged();

private:
    QStringList m_files;
    int m_loaded;
};

#endif // FILEPICKER_H
