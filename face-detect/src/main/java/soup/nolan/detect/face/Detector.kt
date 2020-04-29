package soup.nolan.detect.face

import soup.nolan.detect.face.model.RawImage

interface Detector {

    fun detect(image: RawImage)

    fun isInDetecting(): Boolean
}
