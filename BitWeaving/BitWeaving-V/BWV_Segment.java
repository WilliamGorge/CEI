
public class BWV_Segment 
{

	private long[] v; 			// Processor words
	private int k;				// Size of one datum
	private int w;				// Size of processor word
	private int Ls;
	private int bit_group_no;
	private int real_width;
	private int word_per_bit_group_no;

	// Default constructor
	public BWV_Segment() 
	{
		k = 0;
		w = 0;
		Ls = 0;
		bit_group_no = 0;
		real_width = 0;
		word_per_bit_group_no = 0;
		v = null;
	}

	// Real constructor
	public BWV_Segment(long[] data_array, int size_of_one_datum, int size_of_processor_word, int bit_group_no_) 
	{
		k = size_of_one_datum;
		w = size_of_processor_word;  
		bit_group_no = bit_group_no_;
		word_per_bit_group_no = k/bit_group_no;
		real_width = w/(word_per_bit_group_no);
		Ls = data_array.length;

		int i, j, l;
		v = new long[bit_group_no];
		long mask, move = ( 1 << (k - 1) );

		// Pour les mots processeurs de 1 à k.
		for (i = 0; i < bit_group_no; ++i)
		{ 		
			v[i] = 0;	// On remplit v[i] de 0.
			
			for (l = 0; l < word_per_bit_group_no; ++l)
			{
				for (j = 0; j < Ls; ++j)
				{
					// On met à 0 tous les bits de data_array[j] sauf le (k-1-i) ième, et l'on affecte le tout à mask.
					mask = data_array[j] & move;
					// On divise par 2 autant qu'il le faut pour que le résultat soit dans le premier bit.
					mask = (mask != 0) ? 1 : 0;
					// On déplace ensuite le bit vers l'emplacement approprié dans le but de l'ajouter à v[i].
					mask <<= (l*real_width + j);
					// On ajoute le résultat à v[i].
					v[i] |= mask;
				}
				
				move >>>= 1;
			}
		}
	}

	public long less_than(long nb)
	{
		// On crée le résultat mlt que l'on initialise à 0.
		long mlt = 0;
		// On crée le mask eq = 111...1(00...0) (autant de 1 que de données dans le segment, plus le reste qu'il faut de 0 vers la gauche).
		long eq = (Ls < 64) ? (1L << Ls) - 1 : ((1L << 63) - 1) + (1L << 63);

		int i = 0, j, l;
		// Nous aurons besoin de deux variables pour copier et déplacer les 10101...
		// mask1 sert à enregistrer les différents bits de la variable à comparer nb
		// mask2 sert à enregistrer le bit (de la donnée en train d'être étudiée) correspondant à celui enregistré dans mask1
		long mask1, mask2;
		// Voici ensuite trois variables qui ont pour but de réduire le nombre d'opérations aléatoires par boucle.
		// En effet, au lieu de gérer 1L << (k - 1) à chaque boucle, on le définit une fois puis le translate progressivement vers la droite.
		// move1 vaut 1 à l'emplacement du bit étudié de nb, et 0 partout ailleurs. Comme on commence par le bit le plus important, on initialise à 2^(k-1).
		// move2 vaut 1 à l'emplacement du bit étudié de v[i] et 0 partout ailleurs. Comme on compare tout d'abord les bits les plus importants, et que les mots sont codés "à l'envers", on initialise à 1.
		// move3 vaut 1 à l'emplacement du bit de eq correspondant à la donnée en train d'être analysée. On initialise à 1 pour les mêmes raisons que move2.
		long move1 = ( 1L << (k - 1) ), move2, move3;

		while (eq != 0 && i < bit_group_no)
		{	
			move2 = 1; // Initialisation à 1 cf. précédemment.
			
			for (l = 0; l < word_per_bit_group_no; ++l)
			{
				mask1 = nb & move1; 			// On copie dans mask1 le bit (l*real_width + j) de nb.
				mask1 = (mask1 != 0) ? 1 : 0;	// Si le bit copié vaut 1, mask1 est non nul et on le met à 1, sinon à 0. Cela permet d'éviter de pratiquer un certain nombre d'opérations élémentaires pour ramener le bit copié au dernier bit.
				move3 = 1;						// Initialisation à 1 cf. précédemment.
	
				for (j = 0; eq != 0 && j < real_width; ++j)
				{	
					if ( ( eq & move3 ) != 0 ) 	// Si eq[j] est encore à 1 càd si son cas est encore indécis.
					{	
						mask2 = v[i] & move2;			// On copie dans mask2 le bit (l*real_width + j) de nb.
						mask2 = (mask2 != 0) ? 1 : 0;	// On copie dans mask2 le bit j du mot processeur (i*word_per_bit_group_no + l)
	
						// Si l'un vaut 1 et l'autre 0, ou si l'un vaut 0 et l'autre 1, alors les deux nombres ne peuvent être égaux.
						if (mask1 !=  mask2) 
						{
							// On passe eq[j] à 0
							eq &= ~move3;
	
							if (mask1 == 1)
								// On stocke le résultat dans mlt.
								mlt |= move3;
						}
					}
					
					move2 <<= 1;	// On passe au bit suivant (v[i])
					move3 <<= 1;	// On passe au bit suivant (eq)
				}
				
				move1 >>>= 1;
			}

			++i;
		}

		return mlt;
	}
	
	public long greater_than(long nb)
	{
		// On crée le résultat mgt que l'on initialise à 0.
		long mgt = 0;
		// On crée le mask eq = 111...1(00...0) (autant de 1 que de données dans le segment, plus le reste qu'il faut de 0 vers la gauche).
		long eq = (Ls < 64) ? (1L << Ls) - 1 : ((1L << 63) - 1) + (1L << 63);

		int i = 0, j, l;
		// Nous aurons besoin de deux variables pour copier et déplacer les 10101...
		// mask1 sert à enregistrer les différents bits de la variable à comparer nb
		// mask2 sert à enregistrer le bit (de la donnée en train d'être étudiée) correspondant à celui enregistré dans mask1
		long mask1, mask2;
		// Voici ensuite trois variables qui ont pour but de réduire le nombre d'opérations aléatoires par boucle.
		// En effet, au lieu de gérer 1L << (k - 1) à chaque boucle, on le définit une fois puis le translate progressivement vers la droite.
		// move1 vaut 1 à l'emplacement du bit étudié de nb, et 0 partout ailleurs. Comme on commence par le bit le plus important, on initialise à 2^(k-1).
		// move2 vaut 1 à l'emplacement du bit étudié de v[i] et 0 partout ailleurs. Comme on compare tout d'abord les bits les plus importants, et que les mots sont codés "à l'envers", on initialise à 1.
		// move3 vaut 1 à l'emplacement du bit de eq correspondant à la donnée en train d'être analysée. On initialise à 1 pour les mêmes raisons que move2.
		long move1 = ( 1L << (k - 1) ), move2, move3;

		while (eq != 0 && i < bit_group_no)
		{	
			move2 = 1; // Initialisation à 1 cf. précédemment.
			
			for (l = 0; l < word_per_bit_group_no; ++l)
			{
				mask1 = nb & move1; 			// On copie dans mask1 le bit (l*real_width + j) de nb.
				mask1 = (mask1 != 0) ? 1 : 0;	// Si le bit copié vaut 1, mask1 est non nul et on le met à 1, sinon à 0. Cela permet d'éviter de pratiquer un certain nombre d'opérations élémentaires pour ramener le bit copié au dernier bit.
				move3 = 1;						// Initialisation à 1 cf. précédemment.
	
				for (j = 0; eq != 0 && j < real_width; ++j)
				{	
					if ( ( eq & move3 ) != 0 ) 	// Si eq[j] est encore à 1 càd si son cas est encore indécis.
					{	
						mask2 = v[i] & move2;			// On copie dans mask2 le bit (l*real_width + j) de nb.
						mask2 = (mask2 != 0) ? 1 : 0;	// On copie dans mask2 le bit j du mot processeur (i*word_per_bit_group_no + l)
	
						// Si l'un vaut 1 et l'autre 0, ou si l'un vaut 0 et l'autre 1, alors les deux nombres ne peuvent être égaux.
						if (mask1 !=  mask2) 
						{
							// On passe eq[j] à 0
							eq &= ~move3;
	
							if (mask1 == 0)
								// On stocke le résultat dans mgt.
								mgt |= move3;
						}
					}
					
					move2 <<= 1;	// On passe au bit suivant (v[i])
					move3 <<= 1;	// On passe au bit suivant (eq)
				}
				
				move1 >>>= 1;
			}

			++i;
		}

		return mgt;
	}
	
	public long equal_to(long nb)
	{
		// On crée le mask eq = 111...1(00...0) (autant de 1 que de données dans le segment, plus le reste qu'il faut de 0 vers la gauche).
		long eq = (Ls < 64) ? (1L << Ls) - 1 : ((1L << 63) - 1) + (1L << 63);

		int i = 0, j, l;
		// Nous aurons besoin de deux variables pour copier et déplacer les 10101...
		// mask1 sert à enregistrer les différents bits de la variable à comparer nb
		// mask2 sert à enregistrer le bit (de la donnée en train d'être étudiée) correspondant à celui enregistré dans mask1
		long mask1, mask2;
		// Voici ensuite trois variables qui ont pour but de réduire le nombre d'opérations aléatoires par boucle.
		// En effet, au lieu de gérer 1L << (k - 1) à chaque boucle, on le définit une fois puis le translate progressivement vers la droite.
		// move1 vaut 1 à l'emplacement du bit étudié de nb, et 0 partout ailleurs. Comme on commence par le bit le plus important, on initialise à 2^(k-1).
		// move2 vaut 1 à l'emplacement du bit étudié de v[i] et 0 partout ailleurs. Comme on compare tout d'abord les bits les plus importants, et que les mots sont codés "à l'envers", on initialise à 1.
		// move3 vaut 1 à l'emplacement du bit de eq correspondant à la donnée en train d'être analysée. On initialise à 1 pour les mêmes raisons que move2.
		long move1 = ( 1L << (k - 1) ), move2, move3;

		while (eq != 0 && i < bit_group_no)
		{	
			move2 = 1; // Initialisation à 1 cf. précédemment.
			
			for (l = 0; l < word_per_bit_group_no; ++l)
			{
				mask1 = nb & move1; 			// On copie dans mask1 le bit (l*real_width + j) de nb.
				mask1 = (mask1 != 0) ? 1 : 0;	// Si le bit copié vaut 1, mask1 est non nul et on le met à 1, sinon à 0. Cela permet d'éviter de pratiquer un certain nombre d'opérations élémentaires pour ramener le bit copié au dernier bit.
				move3 = 1;						// Initialisation à 1 cf. précédemment.
	
				for (j = 0; eq != 0 && j < real_width; ++j)
				{	
					if ( ( eq & move3 ) != 0 ) 	// Si eq[j] est encore à 1 càd si son cas est encore indécis.
					{	
						mask2 = v[i] & move2;			// On copie dans mask2 le bit (l*real_width + j) de nb.
						mask2 = (mask2 != 0) ? 1 : 0;	// On copie dans mask2 le bit j du mot processeur (i*word_per_bit_group_no + l)
	
						// Si l'un vaut 1 et l'autre 0, ou si l'un vaut 0 et l'autre 1, alors les deux nombres ne peuvent être égaux.
						if (mask1 !=  mask2) 
							eq &= ~move3; // On passe eq[j] à 0
					}
					
					move2 <<= 1;	// On passe au bit suivant (v[i])
					move3 <<= 1;	// On passe au bit suivant (eq)
				}
				
				move1 >>>= 1;
			}

			++i;
		}

		return eq;
	}
	
	public long different_to(long nb)
	{
		// On crée le résultat mgt que l'on initialise à 0.
		long diff = 0;
		// On crée le mask eq = 111...1(00...0) (autant de 1 que de données dans le segment, plus le reste qu'il faut de 0 vers la gauche).
		long eq = (Ls < 64) ? (1L << Ls) - 1 : ((1L << 63) - 1) + (1L << 63);

		int i = 0, j, l;
		// Nous aurons besoin de deux variables pour copier et déplacer les 10101...
		// mask1 sert à enregistrer les différents bits de la variable à comparer nb
		// mask2 sert à enregistrer le bit (de la donnée en train d'être étudiée) correspondant à celui enregistré dans mask1
		long mask1, mask2;
		// Voici ensuite trois variables qui ont pour but de réduire le nombre d'opérations aléatoires par boucle.
		// En effet, au lieu de gérer 1L << (k - 1) à chaque boucle, on le définit une fois puis le translate progressivement vers la droite.
		// move1 vaut 1 à l'emplacement du bit étudié de nb, et 0 partout ailleurs. Comme on commence par le bit le plus important, on initialise à 2^(k-1).
		// move2 vaut 1 à l'emplacement du bit étudié de v[i] et 0 partout ailleurs. Comme on compare tout d'abord les bits les plus importants, et que les mots sont codés "à l'envers", on initialise à 1.
		// move3 vaut 1 à l'emplacement du bit de eq correspondant à la donnée en train d'être analysée. On initialise à 1 pour les mêmes raisons que move2.
		long move1 = ( 1L << (k - 1) ), move2, move3;

		while (eq != 0 && i < bit_group_no)
		{	
			move2 = 1; // Initialisation à 1 cf. précédemment.
			
			for (l = 0; l < word_per_bit_group_no; ++l)
			{
				mask1 = nb & move1; 			// On copie dans mask1 le bit (l*real_width + j) de nb.
				mask1 = (mask1 != 0) ? 1 : 0;	// Si le bit copié vaut 1, mask1 est non nul et on le met à 1, sinon à 0. Cela permet d'éviter de pratiquer un certain nombre d'opérations élémentaires pour ramener le bit copié au dernier bit.
				move3 = 1;						// Initialisation à 1 cf. précédemment.
	
				for (j = 0; eq != 0 && j < real_width; ++j)
				{	
					if ( ( eq & move3 ) != 0 ) 	// Si eq[j] est encore à 1 càd si son cas est encore indécis.
					{	
						mask2 = v[i] & move2;			// On copie dans mask2 le bit (l*real_width + j) de nb.
						mask2 = (mask2 != 0) ? 1 : 0;	// On copie dans mask2 le bit j du mot processeur (i*word_per_bit_group_no + l)
	
						// Si l'un vaut 1 et l'autre 0, ou si l'un vaut 0 et l'autre 1, alors les deux nombres ne peuvent être égaux.
						if (mask1 !=  mask2) 
						{
							eq &= ~move3;	// On passe eq[j] à 0
							diff |= move3;
						}
					}
					
					move2 <<= 1;	// On passe au bit suivant (v[i])
					move3 <<= 1;	// On passe au bit suivant (eq)
				}
				
				move1 >>>= 1;
			}
			
			++i;
		}

		return diff;
	}
	
	public long less_than_or_equal_to(long nb)
	{
		// On crée le résultat mlt que l'on initialise à 2^Ls - 1 <=> toutes les données sont supérieures ou égales à nb a priori..
		long mlt = (Ls < 64) ? (1L << Ls) - 1 : ((1L << 63) - 1) + (1L << 63);
		// On crée le mask eq = 111...1(00...0) (autant de 1 que de données dans le segment, plus le reste qu'il faut de 0 vers la gauche).
		long eq = mlt;

		int i = 0, j, l;
		// Nous aurons besoin de deux variables pour copier et déplacer les 10101...
		// mask1 sert à enregistrer les différents bits de la variable à comparer nb
		// mask2 sert à enregistrer le bit (de la donnée en train d'être étudiée) correspondant à celui enregistré dans mask1
		long mask1, mask2;
		// Voici ensuite trois variables qui ont pour but de réduire le nombre d'opérations aléatoires par boucle.
		// En effet, au lieu de gérer 1L << (k - 1) à chaque boucle, on le définit une fois puis le translate progressivement vers la droite.
		// move1 vaut 1 à l'emplacement du bit étudié de nb, et 0 partout ailleurs. Comme on commence par le bit le plus important, on initialise à 2^(k-1).
		// move2 vaut 1 à l'emplacement du bit étudié de v[i] et 0 partout ailleurs. Comme on compare tout d'abord les bits les plus importants, et que les mots sont codés "à l'envers", on initialise à 1.
		// move3 vaut 1 à l'emplacement du bit de eq correspondant à la donnée en train d'être analysée. On initialise à 1 pour les mêmes raisons que move2.
		long move1 = ( 1L << (k - 1) ), move2, move3;

		while (eq != 0 && i < bit_group_no)
		{	
			move2 = 1; // Initialisation à 1 cf. précédemment.
			
			for (l = 0; l < word_per_bit_group_no; ++l)
			{
				mask1 = nb & move1; 			// On copie dans mask1 le bit (l*real_width + j) de nb.
				mask1 = (mask1 != 0) ? 1 : 0;	// Si le bit copié vaut 1, mask1 est non nul et on le met à 1, sinon à 0. Cela permet d'éviter de pratiquer un certain nombre d'opérations élémentaires pour ramener le bit copié au dernier bit.
				move3 = 1;						// Initialisation à 1 cf. précédemment.
	
				for (j = 0; eq != 0 && j < real_width; ++j)
				{	
					if ( ( eq & move3 ) != 0 ) 	// Si eq[j] est encore à 1 càd si son cas est encore indécis.
					{	
						mask2 = v[i] & move2;			// On copie dans mask2 le bit (l*real_width + j) de nb.
						mask2 = (mask2 != 0) ? 1 : 0;	// On copie dans mask2 le bit j du mot processeur (i*word_per_bit_group_no + l)
	
						// Si l'un vaut 1 et l'autre 0, ou si l'un vaut 0 et l'autre 1, alors les deux nombres ne peuvent être égaux.
						if (mask1 !=  mask2) 
						{
							// On passe eq[j] à 0
							eq &= ~move3;
	
							if (mask1 == 0)
								// On stocke le résultat dans mlt.
								mlt &= ~move3;
						}
					}
					
					move2 <<= 1;	// On passe au bit suivant (v[i])
					move3 <<= 1;	// On passe au bit suivant (eq)
				}
				
				move1 >>>= 1;
			}

			++i;
		}
		
		return mlt;
	}

	
	public long greater_than_or_equal_to(long nb)
	{
		// On crée le résultat mgt que l'on initialise à 2^Ls - 1 <=> toutes les données sont supérieures ou égales à nb a priori..
		long mgt = (Ls < 64) ? (1L << Ls) - 1 : ((1L << 63) - 1) + (1L << 63);
		// On crée le mask eq = 111...1(00...0) (autant de 1 que de données dans le segment, plus le reste qu'il faut de 0 vers la gauche).
		long eq = mgt;

		int i = 0, j, l;
		// Nous aurons besoin de deux variables pour copier et déplacer les 10101...
		// mask1 sert à enregistrer les différents bits de la variable à comparer nb
		// mask2 sert à enregistrer le bit (de la donnée en train d'être étudiée) correspondant à celui enregistré dans mask1
		long mask1, mask2;
		// Voici ensuite trois variables qui ont pour but de réduire le nombre d'opérations aléatoires par boucle.
		// En effet, au lieu de gérer 1L << (k - 1) à chaque boucle, on le définit une fois puis le translate progressivement vers la droite.
		// move1 vaut 1 à l'emplacement du bit étudié de nb, et 0 partout ailleurs. Comme on commence par le bit le plus important, on initialise à 2^(k-1).
		// move2 vaut 1 à l'emplacement du bit étudié de v[i] et 0 partout ailleurs. Comme on compare tout d'abord les bits les plus importants, et que les mots sont codés "à l'envers", on initialise à 1.
		// move3 vaut 1 à l'emplacement du bit de eq correspondant à la donnée en train d'être analysée. On initialise à 1 pour les mêmes raisons que move2.
		long move1 = ( 1L << (k - 1) ), move2, move3;

		while (eq != 0 && i < bit_group_no)
		{	
			move2 = 1; // Initialisation à 1 cf. précédemment.
			
			for (l = 0; l < word_per_bit_group_no; ++l)
			{
				mask1 = nb & move1; 			// On copie dans mask1 le bit (l*real_width + j) de nb.
				mask1 = (mask1 != 0) ? 1 : 0;	// Si le bit copié vaut 1, mask1 est non nul et on le met à 1, sinon à 0. Cela permet d'éviter de pratiquer un certain nombre d'opérations élémentaires pour ramener le bit copié au dernier bit.
				move3 = 1;						// Initialisation à 1 cf. précédemment.
	
				for (j = 0; eq != 0 && j < real_width; ++j)
				{	
					if ( ( eq & move3 ) != 0 ) 	// Si eq[j] est encore à 1 càd si son cas est encore indécis.
					{	
						mask2 = v[i] & move2;			// On copie dans mask2 le bit (l*real_width + j) de nb.
						mask2 = (mask2 != 0) ? 1 : 0;	// On copie dans mask2 le bit j du mot processeur (i*word_per_bit_group_no + l)
	
						// Si l'un vaut 1 et l'autre 0, ou si l'un vaut 0 et l'autre 1, alors les deux nombres ne peuvent être égaux.
						if (mask1 !=  mask2) 
						{
							// On passe eq[j] à 0
							eq &= ~move3;
	
							if (mask1 == 1)
								// On stocke le résultat dans mgt.
								mgt &= ~move3;
						}
					}
					
					move2 <<= 1;	// On passe au bit suivant (v[i])
					move3 <<= 1;	// On passe au bit suivant (eq)
				}
				
				move1 >>>= 1;
			}

			++i;
		}

		return mgt;
	}
}
