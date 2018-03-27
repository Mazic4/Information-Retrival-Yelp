
package org.latlab.learner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import org.latlab.model.LTM;
import org.latlab.util.*;

public class ParallelLCMlearner {

	private ParallelEmLearner _emLearner = new ParallelEmLearner();

	// Settings of fullEM on the bestCandModel
	private int _nRestarts, _nMaxSteps;

	/**
	 * The EM threshold.
	 */
	private double _EMthreshold;
	
	private double _BICthreshold = 1;

//	/**
//	 * The Model quality(BIC Score) threshold.
//	 */
//	private double _thresholdBIC = 0.01;

	/**
	 * Specify the settings for global EM.
	 * 
	 * @param nRestarts
	 * @param nMaxSteps
	 * @param threshold
	 */
	public void setEMSettings(int nRestarts, int nMaxSteps, double threshold) {
		_nRestarts = nRestarts;
		_nMaxSteps = nMaxSteps;
		_EMthreshold = threshold;
	}

	private void setEMLearner() {
		_emLearner.setLocalMaximaEscapeMethod("ChickeringHeckerman");
		_emLearner.setReuseFlag(false);
		_emLearner.setNumberOfRestarts(_nRestarts);
		_emLearner.setMaxNumberOfSteps(_nMaxSteps);
		_emLearner.setThreshold(_EMthreshold);
	}
	
	public void setBICThreshold(double threshold)
	{
		_BICthreshold = threshold;
	}

	private String _outputDir;

	public void setOutputDir(String outputDir) {
		_outputDir = outputDir;
	}

	/**
	 * Search starting from an LC model.
	 * 
	 * @param dataSet
	 *            The dataset.
	 * @return An HLC model of highest BIC score.
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public LTM search(DataSet dataSet) throws FileNotFoundException, UnsupportedEncodingException {
		Variable[] maniVars = dataSet.getVariables();
		LTM lCM = LTM.createLCM(maniVars, 2);
		LTM model = search(lCM, dataSet, 2);
		
		return model;
	}

	/**
	 * The LCM learner which starts from an initial LCM and seeks for a best
	 * model.
	 * 
	 * At every step of search, a new LC model is generated by introducing a new
	 * state for the latent variable. The BIC score of the new model is computed
	 * and compared with the previous. The search will terminate when the new
	 * model has worse score then the latter one.
	 * 
	 * @param initModel
	 * @param data
	 * @cardinality the cardinality of the initial model
	 * @return the best model
	 */
	public LTM search(LTM initModel, DataSet data, int cardinality) 
	{
		setEMLearner();
		LTM bestModel = (LTM) _emLearner.em(initModel, data);

		int currentCardinality = cardinality;
		while (true) 
		{
			int nextCardinality = currentCardinality + 1;			
			// Generate next model
			Variable[] maniVars = data.getVariables();
			LTM nextModel = LTM.createLCM(maniVars, nextCardinality);
			
			nextModel = (LTM) _emLearner.em(nextModel, data);

			if (nextModel.getBICScore(data) - bestModel.getBICScore(data) > _BICthreshold) 
			{
				bestModel = nextModel;
				currentCardinality++;
				
			} else
			{
				break;
			}
		}

		return bestModel;
	}
	
	/**
	 * Return the filename of the final model.
	 * 
	 * @return
	 */
	public String getFinalModelFile() {
		return _outputDir + File.separator + "M.LCM.bif";
	}
}