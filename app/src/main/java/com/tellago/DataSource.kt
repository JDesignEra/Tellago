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
                    "New TellSquare office coming soon! Cant wait.",
                "https://www.ajminteriors.com/site/wp-content/uploads/2012/11/construction.jpg",
                    "James",
                "https://firebasestorage.googleapis.com/v0/b/tellago.appspot.com/o/uploads%2Fdp%2FnS1lzldixjYhi7vNvtpgDa0bCux2?alt=media&token=f0ea4560-2964-47bb-80b1-b5f74d763381",
                    "2s ago",
                "129",
                    "35"
                )
            )

            list.add(
                UserPost(
                    "1 more year till I get the keys to my BTO. I am so grateful. Buyer beware, many people are wiping their CPF to get a BTO. My recommendation? Unless you are passively investing (ETF STIs or S&P500), do not wipe your CPF. Instead, consider carrying some of the load with your cash. Why? Inflation and Missed out Interest rates. If your not passively investing, money that you save in your bank will just suffer from inflation. Additionally, by wiping your CPF, you are missing out on compounded interest.",
                    "NIL",
                    "James",
                    "https://firebasestorage.googleapis.com/v0/b/tellago.appspot.com/o/uploads%2Fdp%2FnS1lzldixjYhi7vNvtpgDa0bCux2?alt=media&token=f0ea4560-2964-47bb-80b1-b5f74d763381",
                    "2wks ago",
                    "198",
                    "150"
                )
            )

            list.add(
                UserPost(
                    "Lets talk Insurance! Im relatively inexperienced so do leave comments below for any corrections or tips! What insurance to buy? Heres the two IMPORTANT types of Insurance you need to consider. Health Insurance (MediShield Life and Integrated Shield Plan) AND Life Insurance (wont settle medical bills but will make sure your loved ones are set if you get disabled/die/cannot work!)",
                    "https://www.investopedia.com/thmb/wz64TF5pgVE1tqS_a4OWJ5RbFPc=/2667x2000/smart/filters:no_upscale()/types-of-insurance-policies-you-need-1289675-Final-6f1548b2756741f6944757e8990c7258.png",
                    "James",
                    "https://firebasestorage.googleapis.com/v0/b/tellago.appspot.com/o/uploads%2Fdp%2FnS1lzldixjYhi7vNvtpgDa0bCux2?alt=media&token=f0ea4560-2964-47bb-80b1-b5f74d763381",
                    "1mth ago",
                    "101",
                    "33"
                )
            )
            list.add(
                UserPost(
                    "I recently chanced upon a great SG financial content creator online! Check out their content! @thewokesalaryman",
                    "NIL",
                    "James",
                    "https://firebasestorage.googleapis.com/v0/b/tellago.appspot.com/o/uploads%2Fdp%2FnS1lzldixjYhi7vNvtpgDa0bCux2?alt=media&token=f0ea4560-2964-47bb-80b1-b5f74d763381",
                    "3mths ago",
                    "77",
                    "159"
                )
            )
            list.add(
                UserPost(
                    "Are you investing in ETF STIs or maybe into the S&P500? Its best to start as early as possible. Investing terms may sound confusing to many. But it is so essential to beating inflation. And if your not investing passively early on, your already a step behind the pack! ",
                    "https://upload.wikimedia.org/wikipedia/commons/7/7e/S_and_P_500_chart_1950_to_2016_with_averages.png",
                    "James",
                    "https://firebasestorage.googleapis.com/v0/b/tellago.appspot.com/o/uploads%2Fdp%2FnS1lzldixjYhi7vNvtpgDa0bCux2?alt=media&token=f0ea4560-2964-47bb-80b1-b5f74d763381",
                    "3mths ago",
                    "67",
                    "39"
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