/*
 * This file is part of RskJ
 * Copyright (C) 2017 RSK Labs Ltd.
 * (derived from ethereumJ library, Copyright (c) 2016 <ether.camp>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package co.rsk.validators;

import co.rsk.core.DifficultyCalculator;
import org.ethereum.core.Block;
import org.ethereum.core.BlockHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import static org.ethereum.util.BIUtil.isEqual;

/**
 * Checks block's difficulty against calculated difficulty value
 *
 * @author Mikhail Kalinin
 * @since 02.09.2015
 */
public class BlockDifficultyRule implements BlockParentDependantValidationRule {

    private static final Logger logger = LoggerFactory.getLogger("blockvalidator");

    private final DifficultyCalculator difficultyCalculator;

    public BlockDifficultyRule(DifficultyCalculator difficultyCalculator) {
        this.difficultyCalculator = difficultyCalculator;
    }

    @Override
    public boolean isValid(Block block, Block parent) {
        if(block == null || parent == null) {
            logger.warn("BlockDifficultyRule - block or parent are null");
            return false;
        }
        BlockHeader header = block.getHeader();
        BigInteger calcDifficulty = difficultyCalculator.calcDifficulty(header, parent.getHeader());
        BigInteger difficulty = header.getDifficultyBI();

        if (!isEqual(difficulty, calcDifficulty)) {
            logger.warn(String.format("#%d: difficulty != calcDifficulty", header.getNumber()));
            return false;
        }
        return true;
    }
}
