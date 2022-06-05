# about captcha image
IMAGE_HEIGHT = 70
IMAGE_WIDTH = 200
CHAR_SETS = 'abcdefghijklmnpqrstuvwxyz123456789ABCDEFGHIJKLMNPQRSTUVWXYZ'
CLASSES_NUM = len(CHAR_SETS)
CHARS_NUM = 6
# for train
RECORD_DIR = './data'
TRAIN_FILE = 'train.tfrecords'
VALID_FILE = 'valid.tfrecords'
