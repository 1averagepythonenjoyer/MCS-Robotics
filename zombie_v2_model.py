import tensorflow as tf
from tensorflow.keras import layers, models
from tensorflow.keras.preprocessing.image import ImageDataGenerator

def create_card_detection_model(input_shape):
    model = models.Sequential()
    
    model.add(layers.Conv2D(32, (3, 3), activation='relu', input_shape=input_shape))
    model.add(layers.MaxPooling2D((2, 2)))
    
    model.add(layers.Conv2D(64, (3, 3), activation='relu'))
    model.add(layers.MaxPooling2D((2, 2)))
    
    model.add(layers.Conv2D(64, (3, 3), activation='relu'))
    model.add(layers.Flatten())
    
    model.add(layers.Dense(64, activation='relu'))
    model.add(layers.Dense(1, activation='sigmoid'))  # Binary classification cuz we only need to know if a card is there or not
    
    return model

pixels_to_mm_converter = #depends which camera we use

height_mm = 88 #dimensions piwars gave
width_mm = 62

height_pixels = int(height_mm * pixels_to_mm_converter)
width_pixels = int(width_mm * pixels_to_mm_converter)

input_shape = (height_pixels, width_pixels, 3)
model = create_card_detection_model(input_shape)

model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])

train_datagen = ImageDataGenerator(rescale=1./255)

train_generator = train_datagen.flow_from_directory(
    '',
    target_size=(height_pixels, width_pixels),
    batch_size=32,
    class_mode='binary')

model.fit(train_generator, epochs=10, steps_per_epoch=len(train_generator))

model.save("") #enter in 


