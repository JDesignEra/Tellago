package com.tellago

import com.tellago.models.UserPost

class DataSource {



    companion object{

        fun createDataSet(): ArrayList<UserPost>{
            val list = ArrayList<UserPost>()
            list.add(
                UserPost(
                    "Cactus! Aspire to grow my own plants, starting with this cactus.",
                    "https://www.thespruce.com/thmb/-ynhryW7hP_wM0Rz5ZjGmxNLseE=/3558x3558/smart/filters:no_upscale()/angel-wings-cactus-ab159de1fa074df592d01d4d6799d7c9.jpg",
                    "Carlson",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_donn_felker.png",
                    "2wks ago",
                    "1705",
                    "203"

                )
            )
            list.add(
                UserPost(
                    "Aspire to source for my meals from Singaporean producers only.",
                    "https://www.thoughtco.com/thmb/e5vWvhKU49h0061hqcjqby-iyOU=/4000x2250/smart/filters:no_upscale()/tree-against-white-background-887508916-5c0f078ac9e77c0001c1bf28.jpg",
                    "James",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_vasiliy_zukanov.png",
                    "3wks ago",
                    "1882",
                    "150"
                )
            )
            list.add(
                UserPost(
                    "To plant a hundred trees before I turn 60!",
                    "https://www.thoughtco.com/thmb/e5vWvhKU49h0061hqcjqby-iyOU=/4000x2250/smart/filters:no_upscale()/tree-against-white-background-887508916-5c0f078ac9e77c0001c1bf28.jpg",
                    "James",
                    "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                    "4wks ago",
                    "1980",
                    "215"
                )
            )
            list.add(
                UserPost(
                    "Empower Singaporean households to support environmental causes in a meaningful manner.",
                    "https://www.thespruce.com/thmb/-ynhryW7hP_wM0Rz5ZjGmxNLseE=/3558x3558/smart/filters:no_upscale()/angel-wings-cactus-ab159de1fa074df592d01d4d6799d7c9.jpg",
                    "Saul",
                    "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                    "5wks ago",
                    "2311",
                    "289"
                )
            )
            list.add(
                UserPost(
                    "Terrarium",
                    "https://www.thoughtco.com/thmb/e5vWvhKU49h0061hqcjqby-iyOU=/4000x2250/smart/filters:no_upscale()/tree-against-white-background-887508916-5c0f078ac9e77c0001c1bf28.jpg",
                    "James",
                    "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                    "13wks ago",
                    "2602",
                    "350"
                )
            )
            return list
        }


        // Another function to generate posts for 'Home' fragment RecyclerView
        fun createDataSetForHome(): ArrayList<UserPost>{
            val list = ArrayList<UserPost>()

            list.add(
                UserPost(
                    "Terrarium",
                "https://lmg-labmanager.s3.amazonaws.com/assets/articleNo/1509/aImg/39230/do-plants-think--l.png",
                    "James",
                "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                    "11wks ago",
                "1980",
                    "150"
                )
            )

            list.add(
                UserPost(
                    "Terrarium",
                    "NIL",
                    "James",
                    "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                    "12wks ago",
                    "1980",
                    "150"
                )
            )

            list.add(
                UserPost(
                    "Terrarium",
                    "https://www.thoughtco.com/thmb/e5vWvhKU49h0061hqcjqby-iyOU=/4000x2250/smart/filters:no_upscale()/tree-against-white-background-887508916-5c0f078ac9e77c0001c1bf28.jpg",
                    "James",
                    "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                    "13wks ago",
                    "1980",
                    "150"
                )
            )
            list.add(
                UserPost(
                    "Terrarium",
                    "NIL",
                    "James",
                    "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                    "13wks ago",
                    "1980",
                    "150"
                )
            )
            list.add(
                UserPost(
                    "Terrarium",
                    "https://www.thoughtco.com/thmb/e5vWvhKU49h0061hqcjqby-iyOU=/4000x2250/smart/filters:no_upscale()/tree-against-white-background-887508916-5c0f078ac9e77c0001c1bf28.jpg",
                    "James",
                    "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                    "13wks ago",
                    "1980",
                    "150"
                )
            )
            list.add(
                UserPost(
                    "Terrarium",
                    "https://www.thoughtco.com/thmb/e5vWvhKU49h0061hqcjqby-iyOU=/4000x2250/smart/filters:no_upscale()/tree-against-white-background-887508916-5c0f078ac9e77c0001c1bf28.jpg",
                    "James",
                    "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
                    "13wks ago",
                    "1980",
                    "150"
                )
            )


            /*list.add(
                UserPost(
                    "Terrarium",
                    "Aspire to grow my own plants, starting with my very own terrarium.",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_vasiliy_zukanov.png",
                    "Steven"
                )
            )

            list.add(
                UserPost(
                    "Freelance Android Developer, Donn Felker",
                    "Freelancing as an Android developer with Donn Felker.\\r\\n\\r\\nDonn is also:\\r\\n\\r\\n1) Founder of caster.io\\r\\n\\r\\n2) Co-host of the fragmented podcast (fragmentedpodcast.com).",
                    "https://raw.githubusercontent.com/mitchtabian/Kotlin-RecyclerView-Example/json-data-source/app/src/main/res/drawable/freelance_android_dev_donn_felker.png",
                    "Richelle"
                )
            )
            list.add(
                UserPost(
                    "Congratulations!",
                    "You made it to the end of the course!\r\n\r\nNext we'll be building the REST API!",
                    "https://raw.githubusercontent.com/mitchtabian/Blog-Images/master/digital_ocean.png",
                    "Sally"
                )
            )
            list.add(
                UserPost(
                    "Cactus!",
                    "Aspire to grow my own plants, starting with this cactus.",
                    "https://www.thespruce.com/thmb/-ynhryW7hP_wM0Rz5ZjGmxNLseE=/3558x3558/smart/filters:no_upscale()/angel-wings-cactus-ab159de1fa074df592d01d4d6799d7c9.jpg",
                    "Cathy"
                )
            )

            list.add(
                UserPost(
                    "Baby Shark dududududu Baby Shark dududududu",
                    "Sing along with your friends and family.",
                    "https://firebasestorage.googleapis.com/v0/b/tellago.appspot.com/o/uploads%2Fimages%2Fbabyshark.png?alt=media&token=632caca3-3048-4182-b2bd-9174b6a354d5",
                    "John"
                )
            )*/

            return list
        }

    }
}