#ifndef FILEPICKER_H
#define FILEPICKER_H

#include <QObject>
#include <QProperty>

class FilePicker : public QObject
{
    Q_OBJECT
    Q_PROPERTY(QStringList files READ files WRITE setFiles NOTIFY filesChanged)
    Q_PROPERTY(float loaded READ loaded WRITE setLoaded NOTIFY loadedChanged)

public:
    explicit FilePicker(QObject *parent = nullptr);
    QStringList files() const;
    float loaded() const;
    void setFiles(const QStringList &newFiles);
    void setLoaded(const float &newLoaded);
    Q_INVOKABLE void openFileDialog();

signals:
    void filesChanged();
    void loadedChanged();

private:
    QStringList m_files;
    float m_loaded;
};

#endif // FILEPICKER_H
