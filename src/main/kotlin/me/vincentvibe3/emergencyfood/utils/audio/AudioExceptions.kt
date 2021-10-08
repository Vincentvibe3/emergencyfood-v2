package me.vincentvibe3.emergencyfood.utils.audio

//exceptions used in QueueManager and AudioLoader
class LoadFailedException:Exception() {
}

class QueueAddException:Exception() {
}

class SongNotFoundException:Exception() {
}