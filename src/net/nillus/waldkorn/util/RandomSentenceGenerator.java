package net.nillus.waldkorn.util;

import java.util.Random;

public class RandomSentenceGenerator {
    private final static String[] PREFIXES = { "oh no", "holy shit", "hurray", "LOL", "haha", "ROFL", "LMFAO", "lolwut", "crap", "yay", "son of a bitch!" };
    private final static String[] ENDINGS = { ".", "!!!!111", "?", "...", "!" };
    private final static String[] ASKS = { "has", "what?", "did" };
    private final static String[] ASKS_PLURAL = { "have", "what?", "did" };
    private final static String[] ARTICLES = { "the", "a", "some", "this", "that" };
    private final static String[] ARTICLES_PLURAL = { "those", "these", "some", "the" };
    private final static String[] VERBS = { "danced", "waved", "cried", "walked", "died", "shouted", "fingered", "fucked", "masturbated", "weeped", "painted", "moonwalked", "sung a song", "shivered", "watched", "beavered", "anally fucked", "damned",
            "pwned", "impersonated", "unleashed hell", "blended a banana", "failed" };
    private final static String[] PASSIVEVERBS = { "hugged", "kissed", "drawn", "eaten", "raped", "pushed", "killed", "scared the hell out of", "wanked", "painted", "driven", "fingered", "fried", "tanked", "kicked ass of", "called", "cleaned", "looked",
            "talked", "ended", "waited", "kissed", "washed", "lived", "loved", "begged", "sinned", "played", "stayed", "cried", "studied", "died", "tied", "cut", "fit", "hit", "let", "put", "quit", "set", "shut", "split", "upset", "burst", "cast",
            "cost", "hurt", "spread", "knit", "sat", "spat", "began", "swam", "rang", "sang", "sprang", "clung", "flung", "slung", "stung", "swung", "wrung", "hung", "drank", "shrank", "stank", "thought", "brought", "bought", "sought", "fought",
            "caught", "taught", "crept", "kept", "slept", "swept", "wept", "bled", "bred", "fed", "fled", "led", "sped", "met", "bent", "lent", "sent", "spent", "dealt", "felt", "knelt", "dreamt", "meant", "spilt", "built", "burnt", "held", "sold",
            "told", "found", "ground", "wound", "broke", "chose", "froze", "spoke", "stole", "woke", "wove", "arose", "drove", "rode", "rose", "wrote", "bit", "hid", "slid", "got", "forgot", "gave", "forgave", "forbade", "fell", "swelled", "dove",
            "blew", "flew", "grew", "knew", "threw", "drew", "withdrew", "showed", "ate", "beat", "took", "forsook", "mistook", "shook", "making", "swore", "wore", "tore", "bore", "stood", "understood", "became", "came", "ran", "dug", "spun", "stuck",
            "struck", "did", "went", "had", "heard", "laid", "paid", "said", "lay", "lit", "lost", "left", "proved", "read", "saw", "sewed", "shaved", "shined", "< td>", "won", "was", };
    private final static String[] NOUNS = { "astronaut", "monkey", "Mike Graham", "squirrel", "aardwolf", "indian red admiral", "adouri (unidentified)", "african black crake", "african buffalo", "african bush squirrel", "african clawless otter",
            "african darter", "african elephant", "african fish eagle", "african ground squirrel (unidentified)", "african jacana", "african lion", "african lynx", "african pied wagtail", "african polecat", "african porcupine", "african red-eyed bulbul",
            "african skink", "african snake (unidentified)", "african wild cat", "african wild dog", "agama lizard (unidentified)", "agile wallaby", "agouti", "galapagos albatross", "waved albatross", "american alligator", "mississippi alligator",
            "alpaca", "amazon parrot (unidentified)", "american virginia opossum", "american alligator", "american badger", "american beaver", "american bighorn sheep", "american bison", "american black bear", "american buffalo", "american crow",
            "american marten", "american racer", "american woodcock", "anaconda (unidentified)", "andean goose", "ant (unidentified)", "australian spiny anteater", "giant anteater", "brown antechinus", "antelope ground squirrel", "four-horned antelope",
            "roan antelope", "sable antelope", "arboral spiny rat", "arctic fox", "arctic ground squirrel", "arctic hare", "arctic lemming", "arctic tern", "argalis", "common long-nosed armadillo", "giant armadillo", "nine-banded armadillo",
            "seven-banded armadillo", "asian elephant", "asian false vampire bat", "asian foreset tortoise", "asian lion", "asian openbill", "asian red fox", "asian water buffalo", "asian water dragon", "asiatic jackal", "asiatic wild ass",
            "asiatic wild ass", "australian brush turkey", "australian magpie", "australian masked owl", "australian pelican", "australian sea lion", "australian spiny anteater", "pied avocet", "azara's zorro", "chacma baboon", "gelada baboon",
            "olive baboon", "savanna baboon", "yellow baboon", "american badger", "eurasian badger", "european badger", "honey badger", "bahama pintail", "bald eagle", "baleen whale", "banded mongoose", "long-nosed bandicoot", "short-nosed bandicoot",
            "southern brown bandicoot", "barasingha deer", "black-collared barbet", "crested barbet", "levaillant's barbet", "bare-faced go away bird", "barking gecko", "barrows goldeneye", "asian false vampire bat", "little brown bat",
            "madagascar fruit bat", "bat-eared fox", "bateleur eagle", "american black bear", "black bear", "grizzly bear", "polar bear", "sloth bear", "american beaver", "eurasian beaver", "european beaver", "north american beaver",
            "bee-eater (unidentified)", "carmine bee-eater", "nubian bee-eater", "white-fronted bee-eater", "beisa oryx", "bengal vulture", "bennett's wallaby", "bent-toed gecko", "brush-tailed bettong", "bird (unidentified)", "bare-faced go away bird",
            "black-throated butcher bird", "magnificent frigate bird", "pied butcher bird", "red-billed tropic bird", "secretary bird", "american bison", "black and white colobus", "black bear", "black curlew", "black kite", "black rhinoceros",
            "black spider monkey", "black swan", "black vulture", "black-backed jackal", "black-backed magpie", "black-capped capuchin", "black-capped chickadee", "black-cheeked waxbill", "black-collared barbet", "black-crowned crane",
            "black-crowned night heron", "black-eyed bulbul", "black-faced kangaroo", "black-footed ferret", "black-fronted bulbul", "black-necked stork", "black-tailed deer", "black-tailed prairie dog", "black-tailed tree creeper",
            "black-throated butcher bird", "black-throated cardinal", "black-winged stilt", "red-winged blackbird", "blackbuck", "blackish oystercatcher", "blacksmith plover", "bleeding heart monkey", "blesbok", "blue-breasted cordon bleu",
            "red-cheeked cordon bleu", "blue and gold macaw", "blue and yellow macaw", "blue catfish", "blue crane", "blue duck", "blue fox", "blue peacock", "blue racer", "blue shark", "blue waxbill", "blue wildebeest", "blue-breasted cordon bleu",
            "blue-faced booby", "blue-footed booby", "blue-tongued lizard", "blue-tongued skink", "columbian rainbow boa", "cook's tree boa", "emerald green tree boa", "malagasy ground boa", "mexican boa", "wild boar", "boat-billed heron", "bobcat",
            "bohor reedbuck", "bonnet macaque", "bontebok", "blue-faced booby", "blue-footed booby", "masked booby", "bottle-nose dolphin", "southern boubou", "brazilian otter", "brazilian tapir", "brindled gnu", "brown brocket", "red brocket",
            "brolga crane", "brown and yellow marshbird", "brown antechinus", "brown brocket", "brown capuchin", "brown hyena", "brown lemur", "brown pelican", "brush-tailed bettong", "brush-tailed phascogale", "brush-tailed rat kangaroo",
            "african buffalo", "american buffalo", "asian water buffalo", "wild water buffalo", "african red-eyed bulbul", "black-eyed bulbul", "black-fronted bulbul", "crested bunting", "burchell's gonolek", "burmese black mountain tortoise",
            "burmese brown mountain tortoise", "burrowing owl", "bush dog", "large-eared bushbaby", "bushbuck", "bushpig", "denham's bustard", "kori bustard", "stanley bustard", "butterfly (unidentified)", "canadian tiger swallowtail butterfly",
            "tropical buckeye butterfly", "buttermilk snake", "spectacled caiman", "california sea lion", "dromedary camel", "campo flicker", "canada goose", "canadian river otter", "canadian tiger swallowtail butterfly", "cape barren goose",
            "cape clawless otter", "cape cobra", "cape fox", "cape raven", "cape starling", "cape white-eye", "cape wild cat", "black-capped capuchin", "brown capuchin", "weeper capuchin", "white-fronted capuchin", "capybara", "caracal",
            "caracara (unidentified)", "yellow-headed caracara", "black-throated cardinal", "red-capped cardinal", "caribou", "carmine bee-eater", "carpet python", "carpet snake", "african wild cat", "cape wild cat", "civet cat", "european wild cat",
            "jungle cat", "kaffir cat", "long-tailed spotted cat", "miner's cat", "native cat", "ringtail cat", "tiger cat", "toddy cat", "blue catfish", "cattle egret", "cereopsis goose", "chacma baboon", "chameleon (unidentified)", "cheetah",
            "chestnut weaver", "black-capped chickadee", "chilean flamingo", "chimpanzee", "least chipmunk", "chital", "chuckwalla", "civet (unidentified)", "civet cat", "common palm civet", "small-toothed palm civet", "clark's nutcracker",
            "mocking cliffchat", "ring-tailed coatimundi", "white-nosed coatimundi", "cobra (unidentified)", "cape cobra", "egyptian cobra", "long-billed cockatoo", "red-breasted cockatoo", "red-tailed cockatoo", "roseate cockatoo",
            "slender-billed cockatoo", "sulfur-crested cockatoo", "coke's hartebeest", "collared lemming", "collared lizard", "collared peccary", "black and white colobus", "magistrate black colobus", "white-mantled colobus", "columbian rainbow boa",
            "comb duck", "common boubou shrike", "common brushtail possum", "common dolphin", "common duiker", "common eland", "common genet", "common goldeneye", "common green iguana", "common grenadier", "common langur", "common long-nosed armadillo",
            "common melba finch", "common mynah", "common nighthawk", "common palm civet", "common pheasant", "common raccoon", "common rhea", "common ringtail", "common seal", "common shelduck", "common turkey", "common wallaroo", "common waterbuck",
            "common wolf", "common wombat", "common zebra", "common zorro", "eastern boa constrictor", "cook's tree boa", "red-knobbed coot", "coqui francolin", "coqui partridge", "long-billed corella", "cormorant (unidentified)", "flightless cormorant",
            "great cormorant", "javanese cormorant", "king cormorant", "large cormorant", "little cormorant", "neotropic cormorant", "pied cormorant", "cottonmouth", "cougar", "scottish highland cow", "coyote", "crab (unidentified)", "red lava crab",
            "sally lightfoot crab", "crab-eating fox", "crab-eating raccoon", "african black crake", "black-crowned crane", "blue crane", "brolga crane", "sandhill crane", "sarus crane", "stanley crane", "wattled crane", "black-tailed tree creeper",
            "crested barbet", "crested bunting", "crested porcupine", "crested screamer", "crimson-breasted shrike", "nile crocodile", "american crow", "house crow", "pied crow", "crown of thorns starfish", "crowned eagle", "crowned hawk-eagle", "cuis",
            "black curlew", "currasow (unidentified)", "curve-billed thrasher", "dabchick", "dama wallaby", "dark-winged trumpeter", "african darter", "darwin ground finch (unidentified)", "dassie", "barasingha deer", "black-tailed deer", "mule deer",
            "red deer", "roe deer", "savannah deer", "spotted deer", "swamp deer", "white-tailed deer", "defassa waterbuck", "denham's bustard", "desert kangaroo rat", "desert spiny lizard", "desert tortoise", "tasmanian devil", "kirk's dik dik",
            "dingo", "african wild dog", "black-tailed prairie dog", "bush dog", "raccoon dog", "bottle-nose dolphin", "common dolphin", "striped dolphin", "emerald-spotted wood dove", "galapagos dove", "laughing dove", "little brown dove",
            "mourning collared dove", "ring dove", "rock dove", "white-winged dove", "downy woodpecker", "asian water dragon", "frilled dragon", "komodo dragon", "netted rock dragon", "ornate rock dragon", "western bearded dragon", "russian dragonfly",
            "dromedary camel", "fork-tailed drongo", "blue duck", "comb duck", "mountain duck", "white-faced whistling duck", "common duiker", "gray duiker", "fat-tailed dunnart", "dusky gull", "dusky rattlesnake", "eagle owl (unidentified)",
            "african fish eagle", "bald eagle", "bateleur eagle", "crowned eagle", "golden eagle", "long-crested hawk eagle", "pallas's fish eagle", "tawny eagle", "white-bellied sea eagle", "eastern boa constrictor", "eastern box turtle",
            "eastern cottontail rabbit", "eastern diamondback rattlesnake", "eastern dwarf mongoose", "eastern fox squirrel", "eastern grey kangaroo", "eastern indigo snake", "eastern quoll", "eastern white pelican", "short-beaked echidna",
            "cattle egret", "great egret", "snowy egret", "egyptian cobra", "egyptian goose", "egyptian viper", "egyptian vulture", "common eland", "elegant crested tinamou", "african elephant", "asian elephant", "eleven-banded armadillo (unidentified)",
            "wapiti elk", "emerald green tree boa", "emerald-spotted wood dove", "emu", "eurasian badger", "eurasian beaver", "eurasian hoopoe", "eurasian red squirrel", "euro wallaby", "european badger", "european beaver", "european red squirrel",
            "european shelduck", "european spoonbill", "european stork", "european wild cat", "fairy penguin", "peregrine falcon", "prairie falcon", "fat-tailed dunnart", "feathertail glider", "feral rock pigeon", "black-footed ferret",
            "ferruginous hawk", "field flicker", "common melba finch", "fisher", "chilean flamingo", "greater flamingo", "lesser flamingo", "roseat flamingo", "campo flicker", "field flicker", "flightless cormorant", "tyrant flycatcher",
            "flying fox (unidentified)", "fork-tailed drongo", "four-horned antelope", "four-spotted skimmer", "four-striped grass mouse", "helmeted guinea fowl", "arctic fox", "asian red fox", "bat-eared fox", "blue fox", "cape fox", "crab-eating fox",
            "grey fox", "north american red fox", "pampa gray fox", "savanna fox", "silver-backed fox", "coqui francolin", "swainson's francolin", "frilled dragon", "frilled lizard", "fringe-eared oryx", "frog (unidentified)", "tawny frogmouth", "galah",
            "galapagos albatross", "galapagos dove", "galapagos hawk", "galapagos mockingbird", "galapagos penguin", "galapagos sea lion", "galapagos tortoise", "gambel's quail", "gaur", "grant's gazelle", "thomson's gazelle", "sun gazer",
            "gecko (unidentified)", "barking gecko", "bent-toed gecko", "ring-tailed gecko", "tokay gecko", "gelada baboon", "gemsbok", "common genet", "small-spotted genet", "genoveva", "gerbil (unidentified)", "gerenuk", "giant anteater",
            "giant armadillo", "giant girdled lizard", "giant heron", "giant otter", "gila monster", "giraffe", "feathertail glider", "squirrel glider", "sugar glider", "glossy ibis", "glossy starling (unidentified)", "brindled gnu", "goanna lizard",
            "mountain goat", "hudsonian godwit", "golden brush-tailed possum", "golden eagle", "golden jackal", "golden-mantled ground squirrel", "barrows goldeneye", "common goldeneye", "goliath heron", "burchell's gonolek", "andean goose",
            "canada goose", "cape barren goose", "cereopsis goose", "egyptian goose", "greylag goose", "knob-nosed goose", "snow goose", "spur-winged goose", "western lowland gorilla", "grant's gazelle", "gray duiker", "gray heron", "gray langur",
            "gray rhea", "great cormorant", "great egret", "great horned owl", "great kiskadee", "great skua", "great white pelican", "greater adjutant stork", "greater blue-eared starling", "greater flamingo", "greater kudu", "greater rhea",
            "greater roadrunner", "greater sage grouse", "little grebe", "green heron", "green vine snake", "green-backed heron", "green-winged macaw", "green-winged trumpeter", "common grenadier", "purple grenadier", "grey fox", "grey heron",
            "grey lourie", "grey mouse lemur", "grey phalarope", "grey-footed squirrel", "greylag goose", "griffon vulture", "grison", "grizzly bear", "ground legaan", "ground monitor (unidentified)", "groundhog", "greater sage grouse", "sage grouse",
            "guanaco", "guerza", "dusky gull", "herring gull", "kelp gull", "lava gull", "pacific gull", "silver gull", "southern black-backed gull", "swallow-tail gull", "gulls (unidentified)", "hanuman langur", "harbor seal", "arctic hare",
            "coke's hartebeest", "red hartebeest", "ferruginous hawk", "galapagos hawk", "red-tailed hawk", "crowned hawk-eagle", "hawk-headed parrot", "south african hedgehog", "helmeted guinea fowl", "sage hen", "black-crowned night heron",
            "boat-billed heron", "giant heron", "goliath heron", "gray heron", "green heron", "green-backed heron", "grey heron", "little heron", "striated heron", "yellow-crowned night heron", "herring gull", "hippopotamus", "hoary marmot",
            "hoffman's sloth", "honey badger", "eurasian hoopoe", "leadbeateri's ground hornbill", "red-billed hornbill", "southern ground hornbill", "yellow-billed hornbill", "horned lark", "horned puffin", "horned rattlesnake", "hottentot teal",
            "house crow", "house sparrow", "hudsonian godwit", "hummingbird (unidentified)", "huron", "brown hyena", "spotted hyena", "striped hyena", "hyrax", "ibex", "glossy ibis", "puna ibis", "sacred ibis", "common green iguana", "land iguana",
            "marine iguana", "impala", "indian giant squirrel", "indian jackal", "indian leopard", "indian mynah", "indian peacock", "indian porcupine", "indian red admiral", "indian star tortoise", "indian tree pie", "stick insect", "jabiru stork",
            "african jacana", "asiatic jackal", "black-backed jackal", "golden jackal", "indian jackal", "silver-backed jackal", "white-tailed jackrabbit", "long-tailed jaeger", "jaguar", "jaguarundi", "japanese macaque", "javan gold-spotted mongoose",
            "javanese cormorant", "jungle cat", "jungle kangaroo", "kaffir cat", "kafue flats lechwe", "kalahari scrub robin", "black-faced kangaroo", "brush-tailed rat kangaroo", "eastern grey kangaroo", "jungle kangaroo", "red kangaroo",
            "western grey kangaroo", "kelp gull", "killer whale", "king cormorant", "king vulture", "malachite kingfisher", "pied kingfisher", "white-throated kingfisher", "kinkajou", "kirk's dik dik", "great kiskadee", "black kite", "klipspringer",
            "knob-nosed goose", "koala", "komodo dragon", "kongoni", "laughing kookaburra", "kori bustard", "greater kudu", "land iguana", "common langur", "gray langur", "hanuman langur", "lappet-faced vulture", "lapwing (unidentified)",
            "southern lapwing", "large cormorant", "large-eared bushbaby", "horned lark", "laughing dove", "laughing kookaburra", "lava gull", "leadbeateri's ground hornbill", "least chipmunk", "kafue flats lechwe", "monitor (unidentified) legaan",
            "ground legaan", "water legaan", "arctic lemming", "collared lemming", "brown lemur", "grey mouse lemur", "lesser mouse lemur", "ring-tailed lemur", "sportive lemur", "leopard", "indian leopard", "lesser double-collared sunbird",
            "lesser flamingo", "lesser masked weaver", "lesser mouse lemur", "levaillant's barbet", "lilac-breasted roller", "lily trotter", "african lion", "asian lion", "australian sea lion", "california sea lion", "galapagos sea lion",
            "mountain lion", "south american sea lion", "southern sea lion", "steller sea lion", "steller's sea lion", "little blue penguin", "little brown bat", "little brown dove", "little cormorant", "little grebe", "little heron",
            "lizard (unidentified)", "blue-tongued lizard", "collared lizard", "desert spiny lizard", "frilled lizard", "giant girdled lizard", "goanna lizard", "mexican beaded lizard", "llama", "long-billed cockatoo", "long-billed corella",
            "long-crested hawk eagle", "long-finned pilot whale", "long-necked turtle", "long-nosed bandicoot", "long-tailed jaeger", "long-tailed skua", "long-tailed spotted cat", "scaly-breasted lorikeet", "slender loris", "rainbow lory",
            "grey lourie", "african lynx", "bonnet macaque", "japanese macaque", "pig-tailed macaque", "rhesus macaque", "blue and gold macaw", "blue and yellow macaw", "green-winged macaw", "red and blue macaw", "scarlet macaw", "madagascar fruit bat",
            "madagascar hawk owl", "magellanic penguin", "magistrate black colobus", "magnificent frigate bird", "australian magpie" };

    private final static String[] ADJECTIVES = { "adorable", "adventurous", "aggressive", "alert", "attractive", "average", "beautiful", "blue-eyed", "bloody", "blushing", "bright", "clean", "clear", "cloudy", "colorful", "crowded", "cute", "dark",
            "drab", "distinct", "dull", "elegant", "excited", "fancy", "filthy", "glamorous", "gleaming", "gorgeous", "graceful", "grotesque", "handsome", "homely", "light", "long", "magnificent", "misty", "motionless", "muddy", "old-fashioned", "plain",
            "poised", "precious", "quaint", "shiny", "smoggy", "sparkling", "spotless", "stormy", "strange", "ugly", "ugliest", "unsightly", "unusual", "wide-eyed", "alive", "annoying", "bad", "better", "beautiful", "brainy", "breakable", "busy",
            "careful", "cautious", "clever", "clumsy", "concerned", "crazy", "curious", "dead", "different", "difficult", "doubtful", "easy", "expensive", "famous", "fragile", "frail", "gifted", "helpful", "helpless", "horrible", "important",
            "impossible", "inexpensive", "innocent", "inquisitive", "modern", "mushy", "odd", "open", "outstanding", "poor", "powerful", "prickly", "puzzled", "real", "rich", "shy", "sleepy", "stupid", "super", "talented", "tame", "tender", "tough",
            "uninterested", "vast", "wandering", "wild", "wrong", "angry", "annoyed", "anxious", "arrogant", "ashamed", "awful", "bad", "bewildered", "black", "blue", "bored", "clumsy", "combative", "condemned", "confused", "crazy, flipped-out",
            "creepy", "cruel", "dangerous", "defeated", "defiant", "depressed", "disgusted", "disturbed", "dizzy", "dull", "embarrassed", "envious", "evil", "fierce", "foolish", "frantic", "frightened", "grieving", "grumpy", "helpless", "homeless",
            "hungry", "hurt", "ill", "itchy", "jealous", "jittery", "lazy", "lonely", "mysterious", "nasty", "naughty", "nervous", "nutty", "obnoxious", "outrageous", "panicky", "repulsive", "scary", "selfish", "sore", "tense", "terrible", "testy",
            "thoughtless", "tired", "troubled", "upset", "uptight", "weary", "wicked", "worried", "agreeable", "amused", "brave", "calm", "charming", "cheerful", "comfortable", "cooperative", "courageous", "delightful", "determined", "eager", "elated",
            "enchanting", "encouraging", "energetic", "enthusiastic", "excited", "exuberant", "fair", "faithful", "fantastic", "fine", "friendly", "funny", "gentle", "glorious", "good", "happy", "healthy", "helpful", "hilarious", "jolly", "joyous",
            "kind", "lively", "lovely", "lucky", "nice", "obedient", "perfect", "pleasant", "proud", "relieved", "silly", "smiling", "splendid", "successful", "thankful", "thoughtful", "victorious", "vivacious", "witty", "wonderful", "zealous", "zany",
            "broad", "chubby", "crooked", "curved", "deep", "flat", "high", "hollow", "low", "narrow", "round", "shallow", "skinny", "square", "steep", "straight", "wide", "big", "colossal", "fat", "gigantic", "great", "huge", "immense", "large",
            "little", "mammoth", "massive", "miniature", "petite", "puny", "scrawny", "short", "small", "tall", "teeny", "teeny-tiny", "tiny", "cooing", "deafening", "faint", "harsh", "high-pitched", "hissing", "hushed", "husky", "loud", "melodic",
            "moaning", "mute", "noisy", "purring", "quiet", "raspy", "resonant", "screeching", "shrill", "silent", "soft", "squealing", "thundering", "voiceless", "whispering", "ancient", "brief", "Early", "fast", "late", "long", "modern", "old",
            "old-fashioned", "quick", "rapid", "short", "slow", "swift", "young", "bitter", "delicious", "fresh", "juicy", "ripe", "rotten", "salty", "sour", "spicy", "stale", "sticky", "strong", "sweet", "tart", "tasteless", "tasty", "thirsty",
            "fluttering", "fuzzy", "greasy", "grubby", "hard", "hot", "icy", "loose", "melted", "nutritious", "plastic", "prickly", "rainy", "rough", "scattered", "shaggy", "shaky", "sharp", "shivering", "silky", "slimy", "slippery", "smooth", "soft",
            "solid", "steady", "sticky", "tender", "tight", "uneven", "weak", "wet", "wooden", "yummy", "boiling", "breezy", "broken", "bumpy", "chilly", "cold", "cool", "creepy", "crooked", "cuddly", "curly", "damaged", "damp", "dirty", "dry", "dusty",
            "filthy", "flaky", "fluffy", "freezing", "hot", "warm", "wet", "abundant", "empty", "few", "heavy", "light", "many", "numerous", "substantial" };

    private static Random m_random = new Random();

    public static String generateSentence()
    {
        boolean isAsking = m_random.nextBoolean();
        boolean isPlural = m_random.nextBoolean();
        StringBuilder sb = new StringBuilder();

        // prefix?
        if (m_random.nextInt(4) == 2)
        {
            sb.append(getRndElement(PREFIXES));
            sb.append(getRndElement(ENDINGS));
            sb.append(' ');
        }

        // Asking?
        if (isAsking)
        {
            sb.append(getRndElement(isPlural ? ASKS_PLURAL : ASKS));
            sb.append(' ');
        }

        // Article
        sb.append(isPlural ? getRndElement(ARTICLES_PLURAL) : getRndElement(ARTICLES));
        sb.append(' ');

        // Adjective
        if (m_random.nextBoolean())
        {
            sb.append(getRndElement(ADJECTIVES));
            sb.append(' ');
        }

        // Noun
        String noun1 = getRndElement(NOUNS);
        sb.append(getRndElement(NOUNS));
        if (isPlural)
        {
            char ending = noun1.charAt(noun1.length() - 1);
            if (ending == 'e' || ending == 'k' || ending == 't' || ending == 'r')
            {
                sb.append('s');
            }
            else
            {
                sb.append("es");
            }
        }
        sb.append(' ');

        // Another noun?
        if (m_random.nextBoolean() && m_random.nextBoolean())
        {
            if (m_random.nextInt(8) == 6)
            {
                sb.append("with");
            }
            else
            {
                sb.append(getRndElement(PASSIVEVERBS));
            }
            sb.append(' ');
            sb.append(isPlural ? getRndElement(ARTICLES_PLURAL) : getRndElement(ARTICLES));
            sb.append(' ');

            // Adjective
            if (m_random.nextBoolean())
            {
                sb.append(getRndElement(ADJECTIVES));
                sb.append(' ');
            }

            String noun2 = getRndElement(NOUNS);
            sb.append(noun2);
            if (isPlural)
            {
                char ending = noun1.charAt(noun1.length() - 1);
                if (ending == 'e' || ending == 'k' || ending == 't' || ending == 'r')
                {
                    sb.append('s');
                }
                else
                {
                    sb.append("es");
                }
            }
        }
        else
        {
            // Just verb
            sb.append(getRndElement(VERBS));
        }

        // Closing
        if (isAsking)
        {
            sb.append('?');
        }
        else
        {
            sb.append(getRndElement(ENDINGS));
        }

        // capitalize first letter
        String str = sb.toString();
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    private static String getRndElement(String[] elements)
    {
        return elements[m_random.nextInt(elements.length)];
    }
}

